package br.com.atai.dao;

import br.com.atai.model.Ata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AtaRepository extends JpaRepository<Ata, Long> {

    boolean existsByReuniaoId(Long reuniaoId);

    Optional<Ata> findByReuniaoId(Long reuniaoId);

    @Query("""
            SELECT DISTINCT a FROM Ata a
            JOIN FETCH a.reuniao r
            JOIN FETCH r.escola
            LEFT JOIN FETCH a.aprovadaPor
            LEFT JOIN FETCH a.alteracoes alt
            LEFT JOIN FETCH alt.prioridade
            WHERE a.id = :id
            """)
    Optional<Ata> findByIdWithDetails(@Param("id") Long id);
}
