package br.com.atai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePrioridadeRequest(
        @NotBlank(message = "Descrição da prioridade é obrigatória.")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres.")
        String descricao,
        @NotNull(message = "Ordem é obrigatória.")
        @Min(value = 1, message = "Ordem deve ser maior que zero.")
        @Max(value = 999, message = "Ordem deve ser menor ou igual a 999.")
        Integer ordem,
        Boolean ativa
) {
}
