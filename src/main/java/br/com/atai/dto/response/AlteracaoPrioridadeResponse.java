package br.com.atai.dto.response;

import br.com.atai.model.AlteracaoPrioridade;
import br.com.atai.model.enums.OperacaoPrioridade;

public record AlteracaoPrioridadeResponse(
        Long id,
        Long ataId,
        Long prioridadeId,
        OperacaoPrioridade operacao,
        String descricaoProposta,
        Integer ordemProposta,
        String justificativa,
        String trechoOrigem
) {
    public static AlteracaoPrioridadeResponse from(AlteracaoPrioridade alteracao) {
        Long prioridadeId = alteracao.getPrioridade() != null ? alteracao.getPrioridade().getId() : null;
        return new AlteracaoPrioridadeResponse(
                alteracao.getId(),
                alteracao.getAta().getId(),
                prioridadeId,
                alteracao.getOperacao(),
                alteracao.getDescricaoProposta(),
                alteracao.getOrdemProposta(),
                alteracao.getJustificativa(),
                alteracao.getTrechoOrigem()
        );
    }
}
