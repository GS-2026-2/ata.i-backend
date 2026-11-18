package br.com.atai.model;

import br.com.atai.model.converter.BooleanSimNaoConverter;
import br.com.atai.model.enums.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Usuário do sistema. Escola obrigatória para perfis escolares; nula para SUPERADMIN
 * (regra garantida pelo BO e pela constraint CK_ATAI_USUARIO_ESCOLA).
 */
@Entity
@Table(name = "T_ATAI_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escola")
    private Escola escola;

    @Column(name = "nm_usuario", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_email", nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "ds_senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_usuario", nullable = false, length = 15)
    private TipoUsuario tipo;

    @Convert(converter = BooleanSimNaoConverter.class)
    @Column(name = "st_ativo", nullable = false, length = 1)
    private boolean ativo = true;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Usuario() {
    }

    public Usuario(Escola escola, String nome, String email, String senhaHash, TipoUsuario tipo) {
        this.escola = escola;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
    }

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
