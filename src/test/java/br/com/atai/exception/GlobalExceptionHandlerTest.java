package br.com.atai.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Teste unitário (sem contexto Spring e sem Oracle) do tratamento genérico de erros.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void unexpectedExceptionShouldReturnGenericBodyWithoutInternalDetails() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/any-path");

        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new IllegalStateException("segredo interno"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(500);
        assertThat(body.path()).isEqualTo("/any-path");
        assertThat(body.message()).isEqualTo("Erro interno inesperado.");
        assertThat(body.message()).doesNotContain("segredo interno");
        assertThat(body.timestamp()).isNotNull();
    }
}
