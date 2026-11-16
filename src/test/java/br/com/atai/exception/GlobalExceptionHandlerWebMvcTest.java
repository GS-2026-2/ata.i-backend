package br.com.atai.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de slice web (sem JPA e sem Oracle) que valida o {@link GlobalExceptionHandler}
 * na camada HTTP. O controller abaixo existe apenas no classpath de testes.
 */
@WebMvcTest(controllers = GlobalExceptionHandlerWebMvcTest.ProbeController.class)
@Import(GlobalExceptionHandlerWebMvcTest.ProbeController.class)
class GlobalExceptionHandlerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void invalidPayloadShouldReturn400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/test-probe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/test-probe"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void malformedJsonShouldReturn400() throws Exception {
        mockMvc.perform(post("/test-probe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou malformado."));
    }

    @Test
    void unsupportedMethodShouldReturn405() throws Exception {
        mockMvc.perform(get("/test-probe"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void unknownRouteShouldReturn404() throws Exception {
        mockMvc.perform(get("/route-that-does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @RestController
    static class ProbeController {

        record ProbeRequest(@NotBlank String name) {
        }

        @PostMapping("/test-probe")
        void probe(@Valid @RequestBody ProbeRequest request) {
            // Sem lógica: existe apenas para disparar o fluxo de validação/conversão do Spring MVC.
        }
    }
}
