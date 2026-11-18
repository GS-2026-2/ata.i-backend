package br.com.atai.bo;

import br.com.atai.dao.AlteracaoPrioridadeRepository;
import br.com.atai.dao.PrioridadeAnualRepository;
import br.com.atai.dto.request.CreatePrioridadeRequest;
import br.com.atai.dto.response.HistoricoPrioridadeResponse;
import br.com.atai.dto.response.PrioridadeAnualResponse;
import br.com.atai.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrioridadeAnualBOTest {

    @Mock
    private PrioridadeAnualRepository prioridadeRepository;

    @Mock
    private AlteracaoPrioridadeRepository alteracaoRepository;

    @Mock
    private EscolaBO escolaBO;

    @InjectMocks
    private PrioridadeAnualBO prioridadeBO;

    @Test
    void criarShouldSavePriorityForActiveSchool() {
        Escola escola = new Escola("E.E. Modelo");
        ReflectionTestUtils.setField(escola, "id", 1L);
        when(escolaBO.buscarEntidadeAtiva(1L)).thenReturn(escola);
        when(prioridadeRepository.save(any(PrioridadeAnual.class))).thenAnswer(inv -> {
            PrioridadeAnual p = inv.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 10L);
            return p;
        });

        PrioridadeAnualResponse response = prioridadeBO.criar(
                new CreatePrioridadeRequest(1L, 2026, "Reformar quadra", 1));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.anoReferencia()).isEqualTo(2026);
        assertThat(response.ativa()).isTrue();
    }

    @Test
    void buscarHistoricoShouldReturnApprovedChangesWithSourceMinutes() {
        Escola escola = new Escola("E.E. Modelo");
        ReflectionTestUtils.setField(escola, "id", 1L);
        Usuario diretor = new Usuario(escola, "Diretor", "d@escola.gov", "h", TipoUsuario.DIRETOR);
        ReflectionTestUtils.setField(diretor, "id", 2L);
        Reuniao reuniao = new Reuniao(escola, diretor, "APM 01", LocalDate.of(2026, 2, 10), 2026, null);
        ReflectionTestUtils.setField(reuniao, "id", 3L);
        Ata ata = new Ata(reuniao, "Texto aprovado");
        ReflectionTestUtils.setField(ata, "id", 4L);
        ata.aprovar(diretor, Instant.now());

        PrioridadeAnual prioridade = new PrioridadeAnual(escola, 2026, "Reformar quadra", 1);
        ReflectionTestUtils.setField(prioridade, "id", 10L);

        AlteracaoPrioridade alt = new AlteracaoPrioridade(
                OperacaoPrioridade.INCLUIR, prioridade, "Reformar quadra", 1, "Urgente", "Linha 12");
        ReflectionTestUtils.setField(alt, "id", 50L);
        ata.adicionarAlteracao(alt);

        when(prioridadeRepository.findByIdWithEscola(10L)).thenReturn(Optional.of(prioridade));
        when(alteracaoRepository.findHistoricoByPrioridadeId(10L, StatusAta.APROVADA)).thenReturn(List.of(alt));

        List<HistoricoPrioridadeResponse> historico = prioridadeBO.buscarHistorico(10L);

        assertThat(historico).hasSize(1);
        assertThat(historico.getFirst().ataId()).isEqualTo(4L);
        assertThat(historico.getFirst().reuniaoTitulo()).isEqualTo("APM 01");
        assertThat(historico.getFirst().justificativa()).isEqualTo("Urgente");
    }

    @Test
    void buscarHistoricoShouldThrow404WhenPriorityNotFound() {
        when(prioridadeRepository.findByIdWithEscola(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prioridadeBO.buscarHistorico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
