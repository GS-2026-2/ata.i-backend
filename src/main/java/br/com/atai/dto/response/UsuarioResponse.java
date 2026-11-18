package br.com.atai.dto.response;

import br.com.atai.model.Usuario;
import br.com.atai.model.enums.TipoUsuario;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        String nome,
        String email,
        TipoUsuario tipo,
        boolean ativo,
        Instant criadoEm
) {
    public static UsuarioResponse from(Usuario usuario) {
        Long escolaId = usuario.getEscola() != null ? usuario.getEscola().getId() : null;
        String escolaNome = usuario.getEscola() != null ? usuario.getEscola().getNome() : null;
        return new UsuarioResponse(
                usuario.getId(),
                escolaId,
                escolaNome,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo(),
                usuario.isAtivo(),
                usuario.getCriadoEm()
        );
    }
}
