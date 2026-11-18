package br.com.atai.dao;

import br.com.atai.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    @Query("""
            SELECT r FROM Reuniao r
            JOIN FETCH r.escola
            JOIN FETCH r.criadaPor
            WHERE (:escolaId IS NULL OR r.escola.id = :escolaId)
              AND (:ano IS NULL OR r.anoReferencia = :ano)
            ORDER BY r.dataReuniao DESC, r.id DESC
            """)
    List<Reuniao> findFiltered(@Param("escolaId") Long escolaId, @Param("ano") Integer ano);

    @Query("SELECT r FROM Reuniao r JOIN FETCH r.escola JOIN FETCH r.criadaPor WHERE r.id = :id")
    Optional<Reuniao> findByIdWithDetails(@Param("id") Long id);
}
