package br.com.atai.bo;

import br.com.atai.dao.AtaRepository;
import br.com.atai.dao.ReuniaoRepository;
import br.com.atai.dto.request.CreateReuniaoRequest;
import br.com.atai.dto.request.UpdateReuniaoRequest;
import br.com.atai.dto.response.ReuniaoResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.model.Ata;
import br.com.atai.model.Escola;
import br.com.atai.model.Reuniao;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoBOTest {

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @Mock
    private AtaRepository ataRepository;

    @Mock
    private EscolaBO escolaBO;

    @Mock
    private UsuarioBO usuarioBO;

    @InjectMocks
    private ReuniaoBO reuniaoBO;

    @Test
    void criarShouldSucceedForCoordenadorOfSameSchool() {
        Escola escola = escola(1L);
        Usuario coord = usuario(2L, escola, TipoUsuario.COORDENADOR, true);
        when(escolaBO.buscarEntidadeAtiva(1L)).thenReturn(escola);
        when(usuarioBO.buscarEntidade(2L)).thenReturn(coord);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(inv -> {
            Reuniao r = inv.getArgument(0);
            ReflectionTestUtils.setField(r, "id", 10L);
            return r;
        });

        ReuniaoResponse response = reuniaoBO.criar(new CreateReuniaoRequest(
                1L, 2L, "Conselho Escolar", LocalDate.of(2026, 3, 5), 2026, null));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.criadaPorId()).isEqualTo(2L);
    }

    @Test
    void criarShouldRejectProfessor() {
        Escola escola = escola(1L);
        Usuario prof = usuario(2L, escola, TipoUsuario.PROFESSOR, true);
        when(escolaBO.buscarEntidadeAtiva(1L)).thenReturn(escola);
        when(usuarioBO.buscarEntidade(2L)).thenReturn(prof);

        assertThatThrownBy(() -> reuniaoBO.criar(new CreateReuniaoRequest(
                1L, 2L, "Reunião", LocalDate.of(2026, 3, 5), 2026, null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void criarShouldRejectUserFromAnotherSchool() {
        Escola escola1 = escola(1L);
        Escola escola2 = escola(2L);
        Usuario diretorOutra = usuario(3L, escola2, TipoUsuario.DIRETOR, true);
        when(escolaBO.buscarEntidadeAtiva(1L)).thenReturn(escola1);
        when(usuarioBO.buscarEntidade(3L)).thenReturn(diretorOutra);

        assertThatThrownBy(() -> reuniaoBO.criar(new CreateReuniaoRequest(
                1L, 3L, "Reunião", LocalDate.of(2026, 3, 5), 2026, null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void atualizarShouldBlockWhenMinutesAreApproved() {
        Escola escola = escola(1L);
        Usuario diretor = usuario(2L, escola, TipoUsuario.DIRETOR, true);
        Reuniao reuniao = new Reuniao(escola, diretor, "Inicial", LocalDate.of(2026, 3, 5), 2026, null);
        ReflectionTestUtils.setField(reuniao, "id", 10L);
        Ata ata = new Ata(reuniao, "Texto");
        ata.aprovar(diretor, Instant.now());

        when(reuniaoRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(reuniao));
        when(ataRepository.findByReuniaoId(10L)).thenReturn(Optional.of(ata));

        assertThatThrownBy(() -> reuniaoBO.atualizar(10L, new UpdateReuniaoRequest(
                "Editada", LocalDate.of(2026, 3, 6), 2026, null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void excluirShouldDeleteDraftMinutesBeforeDeletingMeeting() {
        Escola escola = escola(1L);
        Usuario diretor = usuario(2L, escola, TipoUsuario.DIRETOR, true);
        Reuniao reuniao = new Reuniao(escola, diretor, "Inicial", LocalDate.of(2026, 3, 5), 2026, null);
        ReflectionTestUtils.setField(reuniao, "id", 10L);
        Ata minuta = new Ata(reuniao, "Rascunho");

        when(reuniaoRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(reuniao));
        when(ataRepository.findByReuniaoId(10L)).thenReturn(Optional.of(minuta));

        reuniaoBO.excluir(10L);

        verify(ataRepository).delete(minuta);
        verify(reuniaoRepository).delete(reuniao);
    }

    private static Escola escola(Long id) {
        Escola e = new Escola("Escola " + id);
        ReflectionTestUtils.setField(e, "id", id);
        return e;
    }

    private static Usuario usuario(Long id, Escola escola, TipoUsuario tipo, boolean ativo) {
        Usuario u = new Usuario(escola, "User " + id, "u" + id + "@escola.gov", "hash", tipo);
        u.setAtivo(ativo);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }
}
