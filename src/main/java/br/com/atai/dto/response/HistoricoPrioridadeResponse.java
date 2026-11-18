package br.com.atai.dto.response;

import br.com.atai.model.AlteracaoPrioridade;
import br.com.atai.model.Ata;
import br.com.atai.model.Reuniao;
import br.com.atai.model.enums.OperacaoPrioridade;
import br.com.atai.model.enums.StatusAta;

import java.time.Instant;
import java.time.LocalDate;

public record HistoricoPrioridadeResponse(
        Long alteracaoId,
        Long ataId,
        StatusAta statusAta,
        Instant aprovadaEm,
        Long aprovadaPorId,
        String aprovadaPorNome,
        Long reuniaoId,
        String reuniaoTitulo,
        LocalDate dataReuniao,
        OperacaoPrioridade operacao,
        String descricaoProposta,
        Integer ordemProposta,
        String justificativa,
        String trechoOrigem
) {
    public static HistoricoPrioridadeResponse from(AlteracaoPrioridade alteracao) {
        Ata ata = alteracao.getAta();
        Reuniao reuniao = ata.getReuniao();
        Long aprovadaPorId = ata.getAprovadaPor() != null ? ata.getAprovadaPor().getId() : null;
        String aprovadaPorNome = ata.getAprovadaPor() != null ? ata.getAprovadaPor().getNome() : null;
        return new HistoricoPrioridadeResponse(
                alteracao.getId(),
                ata.getId(),
                ata.getStatus(),
                ata.getAprovadaEm(),
                aprovadaPorId,
                aprovadaPorNome,
                reuniao.getId(),
                reuniao.getTitulo(),
                reuniao.getDataReuniao(),
                alteracao.getOperacao(),
                alteracao.getDescricaoProposta(),
                alteracao.getOrdemProposta(),
                alteracao.getJustificativa(),
                alteracao.getTrechoOrigem()
        );
    }
}
