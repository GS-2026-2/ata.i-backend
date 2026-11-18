package br.com.atai.dao;

import br.com.atai.model.Escola;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscolaRepository extends JpaRepository<Escola, Long> {

    List<Escola> findByAtivaTrueOrderByNomeAsc();

    List<Escola> findAllByOrderByNomeAsc();
}
