package br.com.atai.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Reunião registrada por diretor ou coordenador. A transcrição é opcional
 * quando é enviada uma ata pronta.
 */
@Entity
@Table(name = "T_ATAI_REUNIAO")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reuniao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_escola", nullable = false)
    private Escola escola;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_criador", nullable = false)
    private Usuario criadaPor;

    @Column(name = "ds_titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "dt_reuniao", nullable = false)
    private LocalDate dataReuniao;

    @Column(name = "nr_ano_referencia", nullable = false)
    private int anoReferencia;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ds_transcricao")
    private String transcricao;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant criadaEm;

    protected Reuniao() {
    }

    public Reuniao(Escola escola, Usuario criadaPor, String titulo, LocalDate dataReuniao,
                   int anoReferencia, String transcricao) {
        this.escola = escola;
        this.criadaPor = criadaPor;
        this.titulo = titulo;
        this.dataReuniao = dataReuniao;
        this.anoReferencia = anoReferencia;
        this.transcricao = transcricao;
    }

    @PrePersist
    void prePersist() {
        if (criadaEm == null) {
            criadaEm = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Escola getEscola() {
        return escola;
    }

    public Usuario getCriadaPor() {
        return criadaPor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getDataReuniao() {
        return dataReuniao;
    }

    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }

    public int getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(int anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public String getTranscricao() {
        return transcricao;
    }

    public void setTranscricao(String transcricao) {
        this.transcricao = transcricao;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
