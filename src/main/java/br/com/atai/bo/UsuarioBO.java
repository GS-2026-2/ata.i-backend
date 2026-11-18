package br.com.atai.bo;

import br.com.atai.dao.UsuarioRepository;
import br.com.atai.dto.request.CreateUsuarioRequest;
import br.com.atai.dto.request.UpdateUsuarioRequest;
import br.com.atai.dto.response.UsuarioResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ConflictException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.Escola;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.TipoUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioBO {

    private final UsuarioRepository usuarioRepository;
    private final EscolaBO escolaBO;

    public UsuarioBO(UsuarioRepository usuarioRepository, EscolaBO escolaBO) {
        this.usuarioRepository = usuarioRepository;
        this.escolaBO = escolaBO;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Long escolaId) {
        List<Usuario> usuarios;
        if (escolaId != null) {
            escolaBO.buscarEntidade(escolaId);
            usuarios = usuarioRepository.findByEscolaIdWithEscola(escolaId);
        } else {
            usuarios = usuarioRepository.findAllWithEscola();
        }
        return usuarios.stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse criar(CreateUsuarioRequest request) {
        String email = request.email().trim();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }
        Escola escola = resolverEscola(request.tipo(), request.escolaId());
        Usuario usuario = new Usuario(
                escola,
                request.nome().trim(),
                email,
                request.senhaHash(),
                request.tipo()
        );
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        String email = request.email().trim();
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }
        Escola escola = resolverEscola(request.tipo(), request.escolaId());
        usuario.setEscola(escola);
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setTipo(request.tipo());
        if (request.senhaHash() != null && !request.senhaHash().isBlank()) {
            usuario.setSenhaHash(request.senhaHash());
        }
        if (request.ativo() != null) {
            usuario.setAtivo(request.ativo());
        }
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    Usuario buscarEntidade(Long id) {
        return usuarioRepository.findByIdWithEscola(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private Escola resolverEscola(TipoUsuario tipo, Long escolaId) {
        if (tipo == TipoUsuario.SUPERADMIN) {
            if (escolaId != null) {
                throw new BusinessRuleException("SUPERADMIN não pode ser vinculado a uma escola.");
            }
            return null;
        }
        if (escolaId == null) {
            throw new BusinessRuleException("Escola é obrigatória para o perfil " + tipo + ".");
        }
        return escolaBO.buscarEntidadeAtiva(escolaId);
    }
}
