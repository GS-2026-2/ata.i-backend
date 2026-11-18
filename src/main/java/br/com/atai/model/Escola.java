package br.com.atai.model;

import br.com.atai.model.converter.BooleanSimNaoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "T_ATAI_ESCOLA")
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escola")
    private Long id;

    @Column(name = "nm_escola", nullable = false, length = 120)
    private String nome;

    @Convert(converter = BooleanSimNaoConverter.class)
    @Column(name = "st_ativa", nullable = false, length = 1)
    private boolean ativa = true;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant criadaEm;

    protected Escola() {
    }

    public Escola(String nome) {
        this.nome = nome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
