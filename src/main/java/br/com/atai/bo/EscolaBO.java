package br.com.atai.bo;

import br.com.atai.dao.EscolaRepository;
import br.com.atai.dto.request.CreateEscolaRequest;
import br.com.atai.dto.request.UpdateEscolaRequest;
import br.com.atai.dto.response.EscolaResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.Escola;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscolaBO {

    private final EscolaRepository escolaRepository;

    public EscolaBO(EscolaRepository escolaRepository) {
        this.escolaRepository = escolaRepository;
    }

    @Transactional(readOnly = true)
    public List<EscolaResponse> listar(boolean incluirInativas) {
        List<Escola> escolas = incluirInativas
                ? escolaRepository.findAllByOrderByNomeAsc()
                : escolaRepository.findByAtivaTrueOrderByNomeAsc();
        return escolas.stream().map(EscolaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EscolaResponse buscarPorId(Long id) {
        return EscolaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public EscolaResponse criar(CreateEscolaRequest request) {
        Escola escola = new Escola(request.nome().trim());
        return EscolaResponse.from(escolaRepository.save(escola));
    }

    @Transactional
    public EscolaResponse atualizar(Long id, UpdateEscolaRequest request) {
        Escola escola = buscarEntidade(id);
        escola.setNome(request.nome().trim());
        if (request.ativa() != null) {
            escola.setAtiva(request.ativa());
        }
        return EscolaResponse.from(escolaRepository.save(escola));
    }

    @Transactional
    public void desativar(Long id) {
        Escola escola = buscarEntidade(id);
        escola.setAtiva(false);
        escolaRepository.save(escola);
    }

    Escola buscarEntidade(Long id) {
        return escolaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escola não encontrada: " + id));
    }

    Escola buscarEntidadeAtiva(Long id) {
        Escola escola = buscarEntidade(id);
        if (!escola.isAtiva()) {
            throw new BusinessRuleException("Escola está inativa: " + id);
        }
        return escola;
    }
}
