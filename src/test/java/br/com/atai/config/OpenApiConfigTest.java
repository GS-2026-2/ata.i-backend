package br.com.atai.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void shouldExposeApiMetadata() {
        OpenAPI openApi = new OpenApiConfig().ataiOpenApi();

        assertThat(openApi.getInfo().getTitle()).isEqualTo("ATA.I API");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("0.1.0");
        assertThat(openApi.getInfo().getDescription())
                .isEqualTo("API de gestão de prioridades, reuniões e atas escolares.");
    }
}
