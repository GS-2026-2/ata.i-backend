package br.com.atai.bo;

import br.com.atai.dao.AlteracaoPrioridadeRepository;
import br.com.atai.dao.AtaRepository;
import br.com.atai.dao.PrioridadeAnualRepository;
import br.com.atai.dto.request.AlteracaoPrioridadeRequest;
import br.com.atai.dto.request.AtaTextoRequest;
import br.com.atai.dto.request.ConfirmarAtaRequest;
import br.com.atai.dto.response.AlteracaoPrioridadeResponse;
import br.com.atai.dto.response.AtaResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ConflictException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.AlteracaoPrioridade;
import br.com.atai.model.Ata;
import br.com.atai.model.Escola;
import br.com.atai.model.PrioridadeAnual;
import br.com.atai.model.Reuniao;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.OperacaoPrioridade;
import br.com.atai.model.enums.TipoUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AtaBO {

    private final AtaRepository ataRepository;
    private final AlteracaoPrioridadeRepository alteracaoRepository;
    private final PrioridadeAnualRepository prioridadeRepository;
    private final ReuniaoBO reuniaoBO;
    private final UsuarioBO usuarioBO;
    private final PrioridadeAnualBO prioridadeAnualBO;

    public AtaBO(AtaRepository ataRepository,
                 AlteracaoPrioridadeRepository alteracaoRepository,
                 PrioridadeAnualRepository prioridadeRepository,
                 ReuniaoBO reuniaoBO,
                 UsuarioBO usuarioBO,
                 PrioridadeAnualBO prioridadeAnualBO) {
        this.ataRepository = ataRepository;
        this.alteracaoRepository = alteracaoRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.reuniaoBO = reuniaoBO;
        this.usuarioBO = usuarioBO;
        this.prioridadeAnualBO = prioridadeAnualBO;
    }

    @Transactional
    public AtaResponse criarParaReuniao(Long reuniaoId, AtaTextoRequest request) {
        Reuniao reuniao = reuniaoBO.buscarEntidade(reuniaoId);
        if (ataRepository.existsByReuniaoId(reuniaoId)) {
            throw new ConflictException("Esta reunião já possui uma ata registrada.");
        }
        Ata ata = new Ata(reuniao, request.texto().trim());
        Ata salva = ataRepository.save(ata);
        return AtaResponse.from(buscarEntidade(salva.getId()));
    }

    @Transactional(readOnly = true)
    public AtaResponse buscarPorId(Long id) {
        return AtaResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public AtaResponse buscarPorReuniao(Long reuniaoId) {
        reuniaoBO.buscarEntidade(reuniaoId);
        Ata ata = ataRepository.findByReuniaoId(reuniaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião ainda não possui ata: " + reuniaoId));
        return AtaResponse.from(buscarEntidade(ata.getId()));
    }

    @Transactional
    public AtaResponse atualizarTexto(Long id, AtaTextoRequest request) {
        Ata ata = buscarEntidade(id);
        exigirMinuta(ata);
        ata.setTexto(request.texto().trim());
        ataRepository.save(ata);
        return AtaResponse.from(ata);
    }

    @Transactional
    public void excluir(Long id) {
        Ata ata = buscarEntidade(id);
        exigirMinuta(ata);
        ataRepository.delete(ata);
    }

    @Transactional(readOnly = true)
    public List<AlteracaoPrioridadeResponse> listarAlteracoes(Long ataId) {
        buscarEntidade(ataId);
        return alteracaoRepository.findByAtaIdWithPrioridade(ataId).stream()
                .map(AlteracaoPrioridadeResponse::from)
                .toList();
    }

    @Transactional
    public AlteracaoPrioridadeResponse adicionarAlteracao(Long ataId, AlteracaoPrioridadeRequest request) {
        Ata ata = buscarEntidade(ataId);
        exigirMinuta(ata);
        PrioridadeAnual prioridade = validarProposta(request, ata.getReuniao());
        AlteracaoPrioridade alteracao = new AlteracaoPrioridade(
                request.operacao(),
                prioridade,
                trimToNull(request.descricaoProposta()),
                request.ordemProposta(),
                trimToNull(request.justificativa()),
                trimToNull(request.trechoOrigem())
        );
        ata.adicionarAlteracao(alteracao);
        AlteracaoPrioridade salva = alteracaoRepository.save(alteracao);
        return AlteracaoPrioridadeResponse.from(salva);
    }

    @Transactional
    public AlteracaoPrioridadeResponse atualizarAlteracao(Long ataId, Long alteracaoId, AlteracaoPrioridadeRequest request) {
        AlteracaoPrioridade alteracao = alteracaoRepository.findByIdAndAtaIdWithDetails(alteracaoId, ataId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alteração não encontrada na ata " + ataId + ": " + alteracaoId));
        exigirMinuta(alteracao.getAta());
        PrioridadeAnual prioridade = validarProposta(request, alteracao.getAta().getReuniao());
        alteracao.setOperacao(request.operacao());
        alteracao.setPrioridade(prioridade);
        alteracao.setDescricaoProposta(trimToNull(request.descricaoProposta()));
        alteracao.setOrdemProposta(request.ordemProposta());
        alteracao.setJustificativa(trimToNull(request.justificativa()));
        alteracao.setTrechoOrigem(trimToNull(request.trechoOrigem()));
        return AlteracaoPrioridadeResponse.from(alteracaoRepository.save(alteracao));
    }

    @Transactional
    public void removerAlteracao(Long ataId, Long alteracaoId) {
        AlteracaoPrioridade alteracao = alteracaoRepository.findByIdAndAtaIdWithDetails(alteracaoId, ataId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alteração não encontrada na ata " + ataId + ": " + alteracaoId));
        Ata ata = alteracao.getAta();
        exigirMinuta(ata);
        ata.removerAlteracao(alteracao);
        ataRepository.save(ata);
    }

    /**
     * Confirma a ata (exclusivo do diretor da escola) e aplica todas as alterações
     * propostas na lista anual em uma única transação.
     */
    @Transactional
    public AtaResponse confirmar(Long id, ConfirmarAtaRequest request) {
        Ata ata = buscarEntidade(id);
        exigirMinuta(ata);

        Reuniao reuniao = ata.getReuniao();
        Escola escola = reuniao.getEscola();
        Usuario diretor = validarDiretorAprovador(request.usuarioId(), escola.getId());

        int proximaOrdem = prioridadeRepository.findMaxOrdemAtiva(escola.getId(), reuniao.getAnoReferencia()) + 1;

        for (AlteracaoPrioridade alteracao : ata.getAlteracoes()) {
            proximaOrdem = aplicarAlteracao(alteracao, escola, reuniao.getAnoReferencia(), proximaOrdem);
        }

        ata.aprovar(diretor, Instant.now());
        ataRepository.save(ata);
        return AtaResponse.from(ata);
    }

    private int aplicarAlteracao(AlteracaoPrioridade alteracao, Escola escola, int ano, int proximaOrdem) {
        switch (alteracao.getOperacao()) {
            case INCLUIR -> {
                String descricao = trimToNull(alteracao.getDescricaoProposta());
                if (descricao == null) {
                    throw new BusinessRuleException("Proposta INCLUIR exige descrição.");
                }
                int ordem = alteracao.getOrdemProposta() != null ? alteracao.getOrdemProposta() : proximaOrdem++;
                PrioridadeAnual nova = prioridadeRepository.save(new PrioridadeAnual(escola, ano, descricao, ordem));
                alteracao.setPrioridade(nova);
            }
            case ATUALIZAR -> {
                PrioridadeAnual alvo = validarPrioridadeAlvo(alteracao.getPrioridade(), escola.getId(), ano);
                String descricao = trimToNull(alteracao.getDescricaoProposta());
                if (descricao != null) {
                    alvo.setDescricao(descricao);
                }
                if (alteracao.getOrdemProposta() != null) {
                    alvo.setOrdem(alteracao.getOrdemProposta());
                }
                prioridadeRepository.save(alvo);
            }
            case REMOVER -> {
                PrioridadeAnual alvo = validarPrioridadeAlvo(alteracao.getPrioridade(), escola.getId(), ano);
                alvo.setAtiva(false);
                prioridadeRepository.save(alvo);
            }
        }
        return proximaOrdem;
    }

    private PrioridadeAnual validarProposta(AlteracaoPrioridadeRequest request, Reuniao reuniao) {
        if (request.operacao() == OperacaoPrioridade.INCLUIR) {
            if (request.prioridadeId() != null) {
                throw new BusinessRuleException("Proposta INCLUIR não deve informar prioridade existente.");
            }
            if (trimToNull(request.descricaoProposta()) == null) {
                throw new BusinessRuleException("Proposta INCLUIR exige descrição proposta.");
            }
            return null;
        }
        if (request.prioridadeId() == null) {
            throw new BusinessRuleException("Operação " + request.operacao() + " exige prioridadeId.");
        }
        if (request.operacao() == OperacaoPrioridade.ATUALIZAR
                && trimToNull(request.descricaoProposta()) == null
                && request.ordemProposta() == null) {
            throw new BusinessRuleException("Proposta ATUALIZAR exige descrição proposta ou ordem proposta.");
        }
        PrioridadeAnual prioridade = prioridadeAnualBO.buscarEntidade(request.prioridadeId());
        return validarPrioridadeAlvo(prioridade, reuniao.getEscola().getId(), reuniao.getAnoReferencia());
    }

    private PrioridadeAnual validarPrioridadeAlvo(PrioridadeAnual prioridade, Long escolaId, int ano) {
        if (prioridade == null) {
            throw new BusinessRuleException("Prioridade alvo não informada.");
        }
        if (!prioridade.isAtiva()) {
            throw new BusinessRuleException("Prioridade já está inativa: " + prioridade.getId());
        }
        if (!prioridade.getEscola().getId().equals(escolaId) || prioridade.getAnoReferencia() != ano) {
            throw new BusinessRuleException(
                    "Prioridade " + prioridade.getId() + " não pertence à escola e ao ano da reunião.");
        }
        return prioridade;
    }

    private Usuario validarDiretorAprovador(Long usuarioId, Long escolaId) {
        Usuario usuario = usuarioBO.buscarEntidade(usuarioId);
        if (!usuario.isAtivo()) {
            throw new BusinessRuleException("Usuário aprovador está inativo.");
        }
        if (usuario.getTipo() != TipoUsuario.DIRETOR) {
            throw new BusinessRuleException("Somente um usuário com perfil DIRETOR pode confirmar a ata.");
        }
        if (usuario.getEscola() == null || !usuario.getEscola().getId().equals(escolaId)) {
            throw new BusinessRuleException("O diretor aprovador deve pertencer à escola da reunião.");
        }
        return usuario;
    }

    private void exigirMinuta(Ata ata) {
        if (ata.isAprovada()) {
            throw new BusinessRuleException("Ata já aprovada não pode ser alterada ou excluída.");
        }
    }

    private Ata buscarEntidade(Long id) {
        return ataRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ata não encontrada: " + id));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
