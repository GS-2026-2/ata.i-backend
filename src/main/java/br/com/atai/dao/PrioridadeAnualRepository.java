package br.com.atai.dao;

import br.com.atai.model.PrioridadeAnual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrioridadeAnualRepository extends JpaRepository<PrioridadeAnual, Long> {

    @Query("""
            SELECT p FROM PrioridadeAnual p
            JOIN FETCH p.escola
            WHERE p.ativa = true
              AND (:escolaId IS NULL OR p.escola.id = :escolaId)
              AND (:ano IS NULL OR p.anoReferencia = :ano)
            ORDER BY p.escola.id ASC, p.anoReferencia DESC, p.ordem ASC, p.id ASC
            """)
    List<PrioridadeAnual> findAtivasFiltered(@Param("escolaId") Long escolaId, @Param("ano") Integer ano);

    @Query("SELECT p FROM PrioridadeAnual p JOIN FETCH p.escola WHERE p.id = :id")
    Optional<PrioridadeAnual> findByIdWithEscola(@Param("id") Long id);

    @Query("""
            SELECT COALESCE(MAX(p.ordem), 0) FROM PrioridadeAnual p
            WHERE p.escola.id = :escolaId AND p.anoReferencia = :ano AND p.ativa = true
            """)
    int findMaxOrdemAtiva(@Param("escolaId") Long escolaId, @Param("ano") int ano);
}
