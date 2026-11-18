package br.com.atai.bo;

import br.com.atai.dao.AtaRepository;
import br.com.atai.dao.ReuniaoRepository;
import br.com.atai.dto.request.CreateReuniaoRequest;
import br.com.atai.dto.request.UpdateReuniaoRequest;
import br.com.atai.dto.response.ReuniaoResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.Ata;
import br.com.atai.model.Escola;
import br.com.atai.model.Reuniao;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.TipoUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReuniaoBO {

    private final ReuniaoRepository reuniaoRepository;
    private final AtaRepository ataRepository;
    private final EscolaBO escolaBO;
    private final UsuarioBO usuarioBO;

    public ReuniaoBO(ReuniaoRepository reuniaoRepository,
                     AtaRepository ataRepository,
                     EscolaBO escolaBO,
                     UsuarioBO usuarioBO) {
        this.reuniaoRepository = reuniaoRepository;
        this.ataRepository = ataRepository;
        this.escolaBO = escolaBO;
        this.usuarioBO = usuarioBO;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoResponse> listar(Long escolaId, Integer ano) {
        if (escolaId != null) {
            escolaBO.buscarEntidade(escolaId);
        }
        return reuniaoRepository.findFiltered(escolaId, ano).stream()
                .map(ReuniaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReuniaoResponse buscarPorId(Long id) {
        return ReuniaoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ReuniaoResponse criar(CreateReuniaoRequest request) {
        Escola escola = escolaBO.buscarEntidadeAtiva(request.escolaId());
        Usuario criador = validarCriador(request.usuarioId(), escola.getId());
        Reuniao reuniao = new Reuniao(
                escola,
                criador,
                request.titulo().trim(),
                request.dataReuniao(),
                request.anoReferencia(),
                request.transcricao()
        );
        return ReuniaoResponse.from(reuniaoRepository.save(reuniao));
    }

    @Transactional
    public ReuniaoResponse atualizar(Long id, UpdateReuniaoRequest request) {
        Reuniao reuniao = buscarEntidade(id);
        garantirAtaNaoAprovada(id, "alterar");
        reuniao.setTitulo(request.titulo().trim());
        reuniao.setDataReuniao(request.dataReuniao());
        reuniao.setAnoReferencia(request.anoReferencia());
        reuniao.setTranscricao(request.transcricao());
        return ReuniaoResponse.from(reuniaoRepository.save(reuniao));
    }

    @Transactional
    public void excluir(Long id) {
        Reuniao reuniao = buscarEntidade(id);
        ataRepository.findByReuniaoId(id).ifPresent(ata -> {
            if (ata.isAprovada()) {
                throw new BusinessRuleException("Não é possível excluir reunião com ata já aprovada.");
            }
            ataRepository.delete(ata);
            ataRepository.flush();
        });
        reuniaoRepository.delete(reuniao);
    }

    Reuniao buscarEntidade(Long id) {
        return reuniaoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião não encontrada: " + id));
    }

    private Usuario validarCriador(Long usuarioId, Long escolaId) {
        Usuario usuario = usuarioBO.buscarEntidade(usuarioId);
        if (!usuario.isAtivo()) {
            throw new BusinessRuleException("Usuário criador está inativo.");
        }
        if (usuario.getEscola() == null || !usuario.getEscola().getId().equals(escolaId)) {
            throw new BusinessRuleException("Usuário criador deve pertencer à escola da reunião.");
        }
        if (usuario.getTipo() != TipoUsuario.DIRETOR && usuario.getTipo() != TipoUsuario.COORDENADOR) {
            throw new BusinessRuleException("Apenas DIRETOR ou COORDENADOR pode registrar uma reunião.");
        }
        return usuario;
    }

    private void garantirAtaNaoAprovada(Long reuniaoId, String acao) {
        ataRepository.findByReuniaoId(reuniaoId).ifPresent(ata -> {
            if (ata.isAprovada()) {
                throw new BusinessRuleException("Não é possível " + acao + " reunião com ata já aprovada.");
            }
        });
    }
}
