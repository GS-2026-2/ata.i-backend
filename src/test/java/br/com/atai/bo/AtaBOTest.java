package br.com.atai.bo;

import br.com.atai.dao.AlteracaoPrioridadeRepository;
import br.com.atai.dao.AtaRepository;
import br.com.atai.dao.PrioridadeAnualRepository;
import br.com.atai.dto.request.AlteracaoPrioridadeRequest;
import br.com.atai.dto.request.AtaTextoRequest;
import br.com.atai.dto.request.ConfirmarAtaRequest;
import br.com.atai.dto.response.AtaResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ConflictException;
import br.com.atai.model.AlteracaoPrioridade;
import br.com.atai.model.Ata;
import br.com.atai.model.Escola;
import br.com.atai.model.PrioridadeAnual;
import br.com.atai.model.Reuniao;
import br.com.atai.model.Usuario;
import br.com.atai.model.enums.OperacaoPrioridade;
import br.com.atai.model.enums.StatusAta;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtaBOTest {

    @Mock
    private AtaRepository ataRepository;

    @Mock
    private AlteracaoPrioridadeRepository alteracaoRepository;

    @Mock
    private PrioridadeAnualRepository prioridadeRepository;

    @Mock
    private ReuniaoBO reuniaoBO;

    @Mock
    private UsuarioBO usuarioBO;

    @Mock
    private PrioridadeAnualBO prioridadeAnualBO;

    @InjectMocks
    private AtaBO ataBO;

    @Test
    void criarParaReuniaoShouldThrowConflictWhenMeetingAlreadyHasMinutes() {
        Escola escola = escola(1L);
        Usuario coord = usuario(2L, escola, TipoUsuario.COORDENADOR, true);
        Reuniao reuniao = reuniao(10L, escola, coord, 2026);
        when(reuniaoBO.buscarEntidade(10L)).thenReturn(reuniao);
        when(ataRepository.existsByReuniaoId(10L)).thenReturn(true);

        assertThatThrownBy(() -> ataBO.criarParaReuniao(10L, new AtaTextoRequest("Texto")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void atualizarTextoShouldRejectApprovedMinutes() {
        Escola escola = escola(1L);
        Usuario diretor = usuario(2L, escola, TipoUsuario.DIRETOR, true);
        Reuniao reuniao = reuniao(10L, escola, diretor, 2026);
        Ata ata = new Ata(reuniao, "Original");
        ReflectionTestUtils.setField(ata, "id", 5L);
        ata.aprovar(diretor, Instant.now());

        when(ataRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(ata));

        assertThatThrownBy(() -> ataBO.atualizarTexto(5L, new AtaTextoRequest("Editado")))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void adicionarAlteracaoShouldRejectPriorityFromDifferentYear() {
        Escola escola = escola(1L);
        Usuario coord = usuario(2L, escola, TipoUsuario.COORDENADOR, true);
        Reuniao reuniao = reuniao(10L, escola, coord, 2026);
        Ata ata = new Ata(reuniao, "Minuta");
        ReflectionTestUtils.setField(ata, "id", 5L);

        PrioridadeAnual prioridade2025 = new PrioridadeAnual(escola, 2025, "Antiga", 1);
        ReflectionTestUtils.setField(prioridade2025, "id", 99L);

        when(ataRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(ata));
        when(prioridadeAnualBO.buscarEntidade(99L)).thenReturn(prioridade2025);

        assertThatThrownBy(() -> ataBO.adicionarAlteracao(5L, new AlteracaoPrioridadeRequest(
                OperacaoPrioridade.ATUALIZAR, 99L, "Nova desc", 2, "Motivo", null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void confirmarShouldRejectNonDiretor() {
        Escola escola = escola(1L);
        Usuario coord = usuario(2L, escola, TipoUsuario.COORDENADOR, true);
        Reuniao reuniao = reuniao(10L, escola, coord, 2026);
        Ata ata = new Ata(reuniao, "Minuta");
        ReflectionTestUtils.setField(ata, "id", 5L);

        when(ataRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(ata));
        when(usuarioBO.buscarEntidade(2L)).thenReturn(coord);

        assertThatThrownBy(() -> ataBO.confirmar(5L, new ConfirmarAtaRequest(2L)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void confirmarShouldRejectDiretorFromAnotherSchool() {
        Escola escola1 = escola(1L);
        Escola escola2 = escola(2L);
        Usuario coord = usuario(2L, escola1, TipoUsuario.COORDENADOR, true);
        Usuario outroDiretor = usuario(3L, escola2, TipoUsuario.DIRETOR, true);
        Reuniao reuniao = reuniao(10L, escola1, coord, 2026);
        Ata ata = new Ata(reuniao, "Minuta");
        ReflectionTestUtils.setField(ata, "id", 5L);

        when(ataRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(ata));
        when(usuarioBO.buscarEntidade(3L)).thenReturn(outroDiretor);

        assertThatThrownBy(() -> ataBO.confirmar(5L, new ConfirmarAtaRequest(3L)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void confirmarShouldApplyAllOperationsAndApproveMinutes() {
        Escola escola = escola(1L);
        Usuario diretor = usuario(2L, escola, TipoUsuario.DIRETOR, true);
        Reuniao reuniao = reuniao(10L, escola, diretor, 2026);
        Ata ata = new Ata(reuniao, "Minuta finalizada");
        ReflectionTestUtils.setField(ata, "id", 5L);

        PrioridadeAnual paraAtualizar = new PrioridadeAnual(escola, 2026, "Pintar sala", 1);
        ReflectionTestUtils.setField(paraAtualizar, "id", 100L);
        PrioridadeAnual paraRemover = new PrioridadeAnual(escola, 2026, "Trocar cortinas", 2);
        ReflectionTestUtils.setField(paraRemover, "id", 101L);

        AlteracaoPrioridade incluir = new AlteracaoPrioridade(
                OperacaoPrioridade.INCLUIR, null, "Instalar ventiladores", null, "Calor", "Trecho A");
        ReflectionTestUtils.setField(incluir, "id", 1L);
        AlteracaoPrioridade atualizar = new AlteracaoPrioridade(
                OperacaoPrioridade.ATUALIZAR, paraAtualizar, "Pintar salas e corredor", 1, "Ampliação", "Trecho B");
        ReflectionTestUtils.setField(atualizar, "id", 2L);
        AlteracaoPrioridade remover = new AlteracaoPrioridade(
                OperacaoPrioridade.REMOVER, paraRemover, null, null, "Já resolvido", "Trecho C");
        ReflectionTestUtils.setField(remover, "id", 3L);

        ata.adicionarAlteracao(incluir);
        ata.adicionarAlteracao(atualizar);
        ata.adicionarAlteracao(remover);

        when(ataRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(ata));
        when(usuarioBO.buscarEntidade(2L)).thenReturn(diretor);
        when(prioridadeRepository.findMaxOrdemAtiva(1L, 2026)).thenReturn(2);
        when(prioridadeRepository.save(any(PrioridadeAnual.class))).thenAnswer(inv -> {
            PrioridadeAnual p = inv.getArgument(0);
            if (p.getId() == null) {
                ReflectionTestUtils.setField(p, "id", 102L);
            }
            return p;
        });
        when(ataRepository.save(ata)).thenReturn(ata);

        AtaResponse response = ataBO.confirmar(5L, new ConfirmarAtaRequest(2L));

        assertThat(response.status()).isEqualTo(StatusAta.APROVADA);
        assertThat(response.aprovadaPorId()).isEqualTo(2L);
        assertThat(response.aprovadaEm()).isNotNull();
        assertThat(incluir.getPrioridade()).isNotNull();
        assertThat(incluir.getPrioridade().getId()).isEqualTo(102L);
        assertThat(incluir.getPrioridade().getOrdem()).isEqualTo(3);
        assertThat(paraAtualizar.getDescricao()).isEqualTo("Pintar salas e corredor");
        assertThat(paraRemover.isAtiva()).isFalse();
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

    private static Reuniao reuniao(Long id, Escola escola, Usuario criador, int ano) {
        Reuniao r = new Reuniao(escola, criador, "Reunião " + id, LocalDate.of(ano, 4, 1), ano, null);
        ReflectionTestUtils.setField(r, "id", id);
        return r;
    }
}
