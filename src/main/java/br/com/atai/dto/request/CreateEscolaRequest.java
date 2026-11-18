package br.com.atai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEscolaRequest(
        @NotBlank(message = "Nome da escola é obrigatório.")
        @Size(max = 120, message = "Nome da escola deve ter no máximo 120 caracteres.")
        String nome
) {
}
