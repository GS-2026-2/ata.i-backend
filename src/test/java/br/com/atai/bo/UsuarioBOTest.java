package br.com.atai.bo;

import br.com.atai.dao.UsuarioRepository;
import br.com.atai.dto.request.CreateUsuarioRequest;
import br.com.atai.dto.request.UpdateUsuarioRequest;
import br.com.atai.dto.response.UsuarioResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ConflictException;
import br.com.atai.model.Escola;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioBOTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EscolaBO escolaBO;

    @InjectMocks
    private UsuarioBO usuarioBO;

    @Test
    void criarSuperadminWithoutSchoolShouldSucceed() {
        when(usuarioRepository.existsByEmailIgnoreCase("admin@atai.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 10L);
            return u;
        });

        UsuarioResponse response = usuarioBO.criar(new CreateUsuarioRequest(
                null, "Admin", "admin@atai.com", "hash123", TipoUsuario.SUPERADMIN));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.escolaId()).isNull();
        assertThat(response.tipo()).isEqualTo(TipoUsuario.SUPERADMIN);
    }

    @Test
    void criarSuperadminWithSchoolShouldThrowBusinessRuleException() {
        when(usuarioRepository.existsByEmailIgnoreCase("admin@atai.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioBO.criar(new CreateUsuarioRequest(
                1L, "Admin", "admin@atai.com", "hash123", TipoUsuario.SUPERADMIN)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void criarDiretorWithoutSchoolShouldThrowBusinessRuleException() {
        when(usuarioRepository.existsByEmailIgnoreCase("dir@escola.sp.gov.br")).thenReturn(false);

        assertThatThrownBy(() -> usuarioBO.criar(new CreateUsuarioRequest(
                null, "Diretor", "dir@escola.sp.gov.br", "hash123", TipoUsuario.DIRETOR)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void criarDuplicateEmailShouldThrowConflictException() {
        when(usuarioRepository.existsByEmailIgnoreCase("dup@escola.sp.gov.br")).thenReturn(true);

        assertThatThrownBy(() -> usuarioBO.criar(new CreateUsuarioRequest(
                1L, "Nome", "dup@escola.sp.gov.br", "hash123", TipoUsuario.PROFESSOR)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void criarDiretorWithActiveSchoolShouldSucceed() {
        Escola escola = new Escola("E.E. Central");
        ReflectionTestUtils.setField(escola, "id", 1L);
        when(usuarioRepository.existsByEmailIgnoreCase("dir@escola.sp.gov.br")).thenReturn(false);
        when(escolaBO.buscarEntidadeAtiva(1L)).thenReturn(escola);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 5L);
            return u;
        });

        UsuarioResponse response = usuarioBO.criar(new CreateUsuarioRequest(
                1L, "Maria Diretora", "dir@escola.sp.gov.br", "hash", TipoUsuario.DIRETOR));

        assertThat(response.escolaId()).isEqualTo(1L);
        assertThat(response.escolaNome()).isEqualTo("E.E. Central");
    }

    @Test
    void atualizarWithExistingEmailOnAnotherUserShouldThrowConflictException() {
        Usuario existente = new Usuario(null, "Admin", "a@atai.com", "hash", TipoUsuario.SUPERADMIN);
        ReflectionTestUtils.setField(existente, "id", 1L);
        when(usuarioRepository.findByIdWithEscola(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("outro@atai.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> usuarioBO.atualizar(1L, new UpdateUsuarioRequest(
                null, "Admin", "outro@atai.com", null, TipoUsuario.SUPERADMIN, true)))
                .isInstanceOf(ConflictException.class);
    }
}
