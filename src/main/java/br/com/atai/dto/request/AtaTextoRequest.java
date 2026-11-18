package br.com.atai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtaTextoRequest(
        @NotBlank(message = "Texto da ata é obrigatório.")
        String texto
) {
}
