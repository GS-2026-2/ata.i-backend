package br.com.atai.dao;

import br.com.atai.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.escola ORDER BY u.nome ASC")
    List<Usuario> findAllWithEscola();

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.escola WHERE u.escola.id = :escolaId ORDER BY u.nome ASC")
    List<Usuario> findByEscolaIdWithEscola(@Param("escolaId") Long escolaId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.escola WHERE u.id = :id")
    Optional<Usuario> findByIdWithEscola(@Param("id") Long id);
}
