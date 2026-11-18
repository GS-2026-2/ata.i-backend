package br.com.atai.dto.request;

import br.com.atai.model.enums.OperacaoPrioridade;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlteracaoPrioridadeRequest(
        @NotNull(message = "Operação é obrigatória.")
        OperacaoPrioridade operacao,
        Long prioridadeId,
        @Size(max = 500, message = "Descrição proposta deve ter no máximo 500 caracteres.")
        String descricaoProposta,
        @Min(value = 1, message = "Ordem proposta deve ser maior que zero.")
        @Max(value = 999, message = "Ordem proposta deve ser menor ou igual a 999.")
        Integer ordemProposta,
        @Size(max = 1000, message = "Justificativa deve ter no máximo 1000 caracteres.")
        String justificativa,
        @Size(max = 2000, message = "Trecho de origem deve ter no máximo 2000 caracteres.")
        String trechoOrigem
) {
}
