package br.com.atai.dto.response;

import br.com.atai.model.Ata;
import br.com.atai.model.enums.StatusAta;

import java.time.Instant;
import java.util.List;

public record AtaResponse(
        Long id,
        Long reuniaoId,
        String reuniaoTitulo,
        Long escolaId,
        int anoReferencia,
        String texto,
        StatusAta status,
        Instant criadaEm,
        Instant aprovadaEm,
        Long aprovadaPorId,
        String aprovadaPorNome,
        List<AlteracaoPrioridadeResponse> alteracoes
) {
    public static AtaResponse from(Ata ata) {
        Long aprovadaPorId = ata.getAprovadaPor() != null ? ata.getAprovadaPor().getId() : null;
        String aprovadaPorNome = ata.getAprovadaPor() != null ? ata.getAprovadaPor().getNome() : null;
        List<AlteracaoPrioridadeResponse> alteracoes = ata.getAlteracoes().stream()
                .map(AlteracaoPrioridadeResponse::from)
                .toList();
        return new AtaResponse(
                ata.getId(),
                ata.getReuniao().getId(),
                ata.getReuniao().getTitulo(),
                ata.getReuniao().getEscola().getId(),
                ata.getReuniao().getAnoReferencia(),
                ata.getTexto(),
                ata.getStatus(),
                ata.getCriadaEm(),
                ata.getAprovadaEm(),
                aprovadaPorId,
                aprovadaPorNome,
                alteracoes
        );
    }
}
