package br.com.atai.dto.response;

import br.com.atai.model.PrioridadeAnual;

public record PrioridadeAnualResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        int anoReferencia,
        String descricao,
        int ordem,
        boolean ativa
) {
    public static PrioridadeAnualResponse from(PrioridadeAnual prioridade) {
        return new PrioridadeAnualResponse(
                prioridade.getId(),
                prioridade.getEscola().getId(),
                prioridade.getEscola().getNome(),
                prioridade.getAnoReferencia(),
                prioridade.getDescricao(),
                prioridade.getOrdem(),
                prioridade.isAtiva()
        );
    }
}
