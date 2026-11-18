package br.com.atai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePrioridadeRequest(
        @NotNull(message = "Escola é obrigatória.")
        Long escolaId,
        @NotNull(message = "Ano de referência é obrigatório.")
        @Min(value = 2000, message = "Ano de referência deve ser maior ou igual a 2000.")
        @Max(value = 2100, message = "Ano de referência deve ser menor ou igual a 2100.")
        Integer anoReferencia,
        @NotBlank(message = "Descrição da prioridade é obrigatória.")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres.")
        String descricao,
        @NotNull(message = "Ordem é obrigatória.")
        @Min(value = 1, message = "Ordem deve ser maior que zero.")
        @Max(value = 999, message = "Ordem deve ser menor ou igual a 999.")
        Integer ordem
) {
}
