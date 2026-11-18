package br.com.atai.dto.request;

import jakarta.validation.constraints.NotNull;

public record ConfirmarAtaRequest(
        @NotNull(message = "Usuário aprovador é obrigatório.")
        Long usuarioId
) {
}
