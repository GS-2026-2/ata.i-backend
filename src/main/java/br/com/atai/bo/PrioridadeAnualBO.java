package br.com.atai.bo;

import br.com.atai.dao.AlteracaoPrioridadeRepository;
import br.com.atai.dao.PrioridadeAnualRepository;
import br.com.atai.dto.request.CreatePrioridadeRequest;
import br.com.atai.dto.request.UpdatePrioridadeRequest;
import br.com.atai.dto.response.HistoricoPrioridadeResponse;
import br.com.atai.dto.response.PrioridadeAnualResponse;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.Escola;
import br.com.atai.model.PrioridadeAnual;
import br.com.atai.model.enums.StatusAta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrioridadeAnualBO {

    private final PrioridadeAnualRepository prioridadeRepository;
    private final AlteracaoPrioridadeRepository alteracaoRepository;
    private final EscolaBO escolaBO;

    public PrioridadeAnualBO(PrioridadeAnualRepository prioridadeRepository,
                             AlteracaoPrioridadeRepository alteracaoRepository,
                             EscolaBO escolaBO) {
        this.prioridadeRepository = prioridadeRepository;
        this.alteracaoRepository = alteracaoRepository;
        this.escolaBO = escolaBO;
    }

    @Transactional(readOnly = true)
    public List<PrioridadeAnualResponse> listarVigentes(Long escolaId, Integer ano) {
        if (escolaId != null) {
            escolaBO.buscarEntidade(escolaId);
        }
        return prioridadeRepository.findAtivasFiltered(escolaId, ano).stream()
                .map(PrioridadeAnualResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrioridadeAnualResponse buscarPorId(Long id) {
        return PrioridadeAnualResponse.from(buscarEntidade(id));
    }

    @Transactional
    public PrioridadeAnualResponse criar(CreatePrioridadeRequest request) {
        Escola escola = escolaBO.buscarEntidadeAtiva(request.escolaId());
        PrioridadeAnual prioridade = new PrioridadeAnual(
                escola,
                request.anoReferencia(),
                request.descricao().trim(),
                request.ordem()
        );
        return PrioridadeAnualResponse.from(prioridadeRepository.save(prioridade));
    }

    @Transactional
    public PrioridadeAnualResponse atualizar(Long id, UpdatePrioridadeRequest request) {
        PrioridadeAnual prioridade = buscarEntidade(id);
        prioridade.setDescricao(request.descricao().trim());
        prioridade.setOrdem(request.ordem());
        if (request.ativa() != null) {
            prioridade.setAtiva(request.ativa());
        }
        return PrioridadeAnualResponse.from(prioridadeRepository.save(prioridade));
    }

    @Transactional
    public void desativar(Long id) {
        PrioridadeAnual prioridade = buscarEntidade(id);
        prioridade.setAtiva(false);
        prioridadeRepository.save(prioridade);
    }

    @Transactional(readOnly = true)
    public List<HistoricoPrioridadeResponse> buscarHistorico(Long id) {
        buscarEntidade(id);
        return alteracaoRepository.findHistoricoByPrioridadeId(id, StatusAta.APROVADA).stream()
                .map(HistoricoPrioridadeResponse::from)
                .toList();
    }

    PrioridadeAnual buscarEntidade(Long id) {
        return prioridadeRepository.findByIdWithEscola(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prioridade anual não encontrada: " + id));
    }
}
