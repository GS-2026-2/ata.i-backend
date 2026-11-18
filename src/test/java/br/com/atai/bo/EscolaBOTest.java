package br.com.atai.bo;

import br.com.atai.dao.EscolaRepository;
import br.com.atai.dto.request.CreateEscolaRequest;
import br.com.atai.dto.request.UpdateEscolaRequest;
import br.com.atai.dto.response.EscolaResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.Escola;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscolaBOTest {

    @Mock
    private EscolaRepository escolaRepository;

    @InjectMocks
    private EscolaBO escolaBO;

    @Test
    void criarShouldTrimNameAndSaveActiveSchool() {
        when(escolaRepository.save(any(Escola.class))).thenAnswer(inv -> {
            Escola e = inv.getArgument(0);
            ReflectionTestUtils.setField(e, "id", 1L);
            return e;
        });

        EscolaResponse response = escolaBO.criar(new CreateEscolaRequest("  E.E. Jandira  "));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("E.E. Jandira");
        assertThat(response.ativa()).isTrue();
    }

    @Test
    void buscarPorIdShouldThrow404WhenNotFound() {
        when(escolaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> escolaBO.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void desativarShouldMarkSchoolInactive() {
        Escola escola = new Escola("E.E. Teste");
        ReflectionTestUtils.setField(escola, "id", 1L);
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));

        escolaBO.desativar(1L);

        assertThat(escola.isAtiva()).isFalse();
        verify(escolaRepository).save(escola);
    }

    @Test
    void atualizarShouldUpdateNameAndActiveFlag() {
        Escola escola = new Escola("Antigo");
        ReflectionTestUtils.setField(escola, "id", 1L);
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(escolaRepository.save(escola)).thenReturn(escola);

        EscolaResponse response = escolaBO.atualizar(1L, new UpdateEscolaRequest("Novo Nome", false));

        assertThat(response.nome()).isEqualTo("Novo Nome");
        assertThat(response.ativa()).isFalse();
    }

    @Test
    void buscarEntidadeAtivaShouldRejectInactiveSchool() {
        Escola escola = new Escola("Inativa");
        escola.setAtiva(false);
        ReflectionTestUtils.setField(escola, "id", 2L);
        when(escolaRepository.findById(2L)).thenReturn(Optional.of(escola));

        assertThatThrownBy(() -> escolaBO.buscarEntidadeAtiva(2L))
                .isInstanceOf(BusinessRuleException.class);
    }
}
