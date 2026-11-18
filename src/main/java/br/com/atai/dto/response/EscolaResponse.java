package br.com.atai.dto.response;

import br.com.atai.model.Escola;

import java.time.Instant;

public record EscolaResponse(
        Long id,
        String nome,
        boolean ativa,
        Instant criadaEm
) {
    public static EscolaResponse from(Escola escola) {
        return new EscolaResponse(
                escola.getId(),
                escola.getNome(),
                escola.isAtiva(),
                escola.getCriadaEm()
        );
    }
}
