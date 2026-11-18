package br.com.atai.dao;

import br.com.atai.model.AlteracaoPrioridade;
import br.com.atai.model.enums.StatusAta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlteracaoPrioridadeRepository extends JpaRepository<AlteracaoPrioridade, Long> {

    @Query("""
            SELECT alt FROM AlteracaoPrioridade alt
            LEFT JOIN FETCH alt.prioridade
            WHERE alt.ata.id = :ataId
            ORDER BY alt.id ASC
            """)
    List<AlteracaoPrioridade> findByAtaIdWithPrioridade(@Param("ataId") Long ataId);

    @Query("""
            SELECT alt FROM AlteracaoPrioridade alt
            JOIN FETCH alt.ata a
            JOIN FETCH a.reuniao r
            JOIN FETCH r.escola
            LEFT JOIN FETCH alt.prioridade
            WHERE alt.id = :id AND a.id = :ataId
            """)
    Optional<AlteracaoPrioridade> findByIdAndAtaIdWithDetails(@Param("id") Long id, @Param("ataId") Long ataId);

    @Query("""
            SELECT alt FROM AlteracaoPrioridade alt
            JOIN FETCH alt.ata a
            JOIN FETCH a.reuniao r
            LEFT JOIN FETCH a.aprovadaPor
            WHERE alt.prioridade.id = :prioridadeId
              AND a.status = :status
            ORDER BY a.aprovadaEm DESC, alt.id DESC
            """)
    List<AlteracaoPrioridade> findHistoricoByPrioridadeId(@Param("prioridadeId") Long prioridadeId,
                                                            @Param("status") StatusAta status);
}
