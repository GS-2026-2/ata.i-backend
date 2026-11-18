package br.com.atai.model;

import br.com.atai.model.enums.OperacaoPrioridade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Alteração de prioridade registrada em uma ata. Fica pendente enquanto a ata é MINUTA.
 * Em INCLUIR, a prioridade é nula até a aprovação, quando é criada e vinculada.
 */
@Entity
@Table(name = "T_ATAI_ALTERACAO_PRIORIDADE")
public class AlteracaoPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alteracao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ata", nullable = false)
    private Ata ata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioridade")
    private PrioridadeAnual prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_operacao", nullable = false, length = 10)
    private OperacaoPrioridade operacao;

    @Column(name = "ds_proposta", length = 500)
    private String descricaoProposta;

    @Column(name = "nr_ordem_proposta")
    private Integer ordemProposta;

    @Column(name = "ds_justificativa", length = 1000)
    private String justificativa;

    @Column(name = "ds_trecho_origem", length = 2000)
    private String trechoOrigem;

    protected AlteracaoPrioridade() {
    }

    public AlteracaoPrioridade(OperacaoPrioridade operacao, PrioridadeAnual prioridade,
                               String descricaoProposta, Integer ordemProposta,
                               String justificativa, String trechoOrigem) {
        this.operacao = operacao;
        this.prioridade = prioridade;
        this.descricaoProposta = descricaoProposta;
        this.ordemProposta = ordemProposta;
        this.justificativa = justificativa;
        this.trechoOrigem = trechoOrigem;
    }

    public Long getId() {
        return id;
    }

    public Ata getAta() {
        return ata;
    }

    void setAta(Ata ata) {
        this.ata = ata;
    }

    public PrioridadeAnual getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeAnual prioridade) {
        this.prioridade = prioridade;
    }

    public OperacaoPrioridade getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoPrioridade operacao) {
        this.operacao = operacao;
    }

    public String getDescricaoProposta() {
        return descricaoProposta;
    }

    public void setDescricaoProposta(String descricaoProposta) {
        this.descricaoProposta = descricaoProposta;
    }

    public Integer getOrdemProposta() {
        return ordemProposta;
    }

    public void setOrdemProposta(Integer ordemProposta) {
        this.ordemProposta = ordemProposta;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getTrechoOrigem() {
        return trechoOrigem;
    }

    public void setTrechoOrigem(String trechoOrigem) {
        this.trechoOrigem = trechoOrigem;
    }
}
