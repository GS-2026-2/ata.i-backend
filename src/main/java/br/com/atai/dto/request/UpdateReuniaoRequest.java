package br.com.atai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateReuniaoRequest(
        @NotBlank(message = "Título da reunião é obrigatório.")
        @Size(max = 150, message = "Título deve ter no máximo 150 caracteres.")
        String titulo,
        @NotNull(message = "Data da reunião é obrigatória.")
        LocalDate dataReuniao,
        @NotNull(message = "Ano de referência é obrigatório.")
        @Min(value = 2000, message = "Ano de referência deve ser maior ou igual a 2000.")
        @Max(value = 2100, message = "Ano de referência deve ser menor ou igual a 2100.")
        Integer anoReferencia,
        String transcricao
) {
}
