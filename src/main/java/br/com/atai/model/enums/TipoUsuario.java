package br.com.atai.model.enums;

/**
 * Perfis de acesso do ATA.I.
 * SUPERADMIN tem acesso administrativo global e não pertence a uma escola.
 */
public enum TipoUsuario {
    SUPERADMIN,
    DIRETOR,
    COORDENADOR,
    PROFESSOR
}
