package br.com.atai.dto.response;

import br.com.atai.model.Reuniao;

import java.time.Instant;
import java.time.LocalDate;

public record ReuniaoResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        Long criadaPorId,
        String criadaPorNome,
        String titulo,
        LocalDate dataReuniao,
        int anoReferencia,
        String transcricao,
        Instant criadaEm
) {
    public static ReuniaoResponse from(Reuniao reuniao) {
        return new ReuniaoResponse(
                reuniao.getId(),
                reuniao.getEscola().getId(),
                reuniao.getEscola().getNome(),
                reuniao.getCriadaPor().getId(),
                reuniao.getCriadaPor().getNome(),
                reuniao.getTitulo(),
                reuniao.getDataReuniao(),
                reuniao.getAnoReferencia(),
                reuniao.getTranscricao(),
                reuniao.getCriadaEm()
        );
    }
}
