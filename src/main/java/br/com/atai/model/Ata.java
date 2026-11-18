package br.com.atai.model;

import br.com.atai.model.enums.StatusAta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ata de uma reunião (minuta ou ata pronta). Compõe as alterações de prioridade
 * propostas; somente o diretor a confirma.
 */
@Entity
@Table(name = "T_ATAI_ATA")
public class Ata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ata")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reuniao", nullable = false, unique = true)
    private Reuniao reuniao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_aprovador")
    private Usuario aprovadaPor;

    @Lob
    @Column(name = "ds_texto", nullable = false)
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_ata", nullable = false, length = 10)
    private StatusAta status = StatusAta.MINUTA;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant criadaEm;

    @Column(name = "dt_aprovacao")
    private Instant aprovadaEm;

    @OneToMany(mappedBy = "ata", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<AlteracaoPrioridade> alteracoes = new ArrayList<>();

    protected Ata() {
    }

    public Ata(Reuniao reuniao, String texto) {
        this.reuniao = reuniao;
        this.texto = texto;
    }

    @PrePersist
    void prePersist() {
        if (criadaEm == null) {
            criadaEm = Instant.now();
        }
    }

    public void adicionarAlteracao(AlteracaoPrioridade alteracao) {
        alteracao.setAta(this);
        alteracoes.add(alteracao);
    }

    public void removerAlteracao(AlteracaoPrioridade alteracao) {
        alteracoes.remove(alteracao);
        alteracao.setAta(null);
    }

    /**
     * Marca a ata como aprovada. As validações de negócio (perfil, escola, status)
     * ficam no BO.
     */
    public void aprovar(Usuario diretor, Instant instante) {
        this.aprovadaPor = diretor;
        this.aprovadaEm = instante;
        this.status = StatusAta.APROVADA;
    }

    public boolean isAprovada() {
        return status == StatusAta.APROVADA;
    }

    public Long getId() {
        return id;
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    public Usuario getAprovadaPor() {
        return aprovadaPor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public StatusAta getStatus() {
        return status;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAprovadaEm() {
        return aprovadaEm;
    }

    public List<AlteracaoPrioridade> getAlteracoes() {
        return Collections.unmodifiableList(alteracoes);
    }
}
