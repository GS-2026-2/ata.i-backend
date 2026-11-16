package br.com.atai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static final String TITLE = "ATA.I API";
    static final String VERSION = "0.1.0";
    static final String DESCRIPTION = "API de gestão de prioridades, reuniões e atas escolares.";

    @Bean
    public OpenAPI ataiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .version(VERSION)
                        .description(DESCRIPTION));
    }
}
