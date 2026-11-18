package br.com.atai.model;

import br.com.atai.model.converter.BooleanSimNaoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Prioridade da lista anual de uma escola. Mantém o identificador entre reuniões;
 * remoção é lógica ({@code ativa = false}).
 */
@Entity
@Table(name = "T_ATAI_PRIORIDADE_ANUAL")
public class PrioridadeAnual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_escola", nullable = false)
    private Escola escola;

    @Column(name = "nr_ano_referencia", nullable = false)
    private int anoReferencia;

    @Column(name = "ds_prioridade", nullable = false, length = 500)
    private String descricao;

    @Column(name = "nr_ordem", nullable = false)
    private int ordem;

    @Convert(converter = BooleanSimNaoConverter.class)
    @Column(name = "st_ativa", nullable = false, length = 1)
    private boolean ativa = true;

    protected PrioridadeAnual() {
    }

    public PrioridadeAnual(Escola escola, int anoReferencia, String descricao, int ordem) {
        this.escola = escola;
        this.anoReferencia = anoReferencia;
        this.descricao = descricao;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public Escola getEscola() {
        return escola;
    }

    public int getAnoReferencia() {
        return anoReferencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
