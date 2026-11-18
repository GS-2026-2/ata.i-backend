package br.com.atai.controller;

import br.com.atai.bo.AtaBO;
import br.com.atai.bo.EscolaBO;
import br.com.atai.bo.PrioridadeAnualBO;
import br.com.atai.bo.ReuniaoBO;
import br.com.atai.bo.UsuarioBO;
import br.com.atai.dto.response.AtaResponse;
import br.com.atai.dto.response.EscolaResponse;
import br.com.atai.exception.BusinessRuleException;
import br.com.atai.exception.ConflictException;
import br.com.atai.exception.ResourceNotFoundException;
import br.com.atai.model.enums.StatusAta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        EscolaController.class,
        UsuarioController.class,
        PrioridadeAnualController.class,
        ReuniaoController.class,
        AtaController.class
})
class ApiControllersWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EscolaBO escolaBO;

    @MockitoBean
    private UsuarioBO usuarioBO;

    @MockitoBean
    private PrioridadeAnualBO prioridadeAnualBO;

    @MockitoBean
    private ReuniaoBO reuniaoBO;

    @MockitoBean
    private AtaBO ataBO;

    @Test
    void postEscolaShouldReturn201WithLocation() throws Exception {
        when(escolaBO.criar(any()))
                .thenReturn(new EscolaResponse(1L, "E.E. Jandira", true, Instant.now()));

        mockMvc.perform(post("/api/escolas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"E.E. Jandira\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/escolas/1"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void postEscolaBlankNameShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/escolas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("nome"));
    }

    @Test
    void deleteEscolaShouldReturn204() throws Exception {
        doNothing().when(escolaBO).desativar(1L);

        mockMvc.perform(delete("/api/escolas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPrioridadeHistoricoWhenMissingShouldReturn404() throws Exception {
        when(prioridadeAnualBO.buscarHistorico(99L))
                .thenThrow(new ResourceNotFoundException("Prioridade anual não encontrada: 99"));

        mockMvc.perform(get("/api/prioridades/99/historico"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void postReuniaoAtaWhenAlreadyExistsShouldReturn409() throws Exception {
        when(ataBO.criarParaReuniao(eq(10L), any()))
                .thenThrow(new ConflictException("Esta reunião já possui uma ata registrada."));

        mockMvc.perform(post("/api/reunioes/10/ata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"texto\":\"Texto da ata\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void postConfirmarAtaShouldReturn200WhenValid() throws Exception {
        AtaResponse approved = new AtaResponse(
                5L, 10L, "Reunião APM", 1L, 2026, "Texto",
                StatusAta.APROVADA, Instant.now(), Instant.now(), 2L, "Diretor", List.of());
        when(ataBO.confirmar(eq(5L), any())).thenReturn(approved);

        mockMvc.perform(post("/api/atas/5/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    void postConfirmarAtaWhenBusinessRuleViolatedShouldReturn422() throws Exception {
        when(ataBO.confirmar(eq(5L), any()))
                .thenThrow(new BusinessRuleException("Somente um usuário com perfil DIRETOR pode confirmar a ata."));

        mockMvc.perform(post("/api/atas/5/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":3}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }
}
