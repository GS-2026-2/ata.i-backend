package br.com.atai.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Interrompe a inicialização, antes de qualquer tentativa de conexão, quando
 * as variáveis obrigatórias do Oracle não foram informadas.
 * Evita erros pouco claros do driver (ex.: "claims to not accept jdbcUrl, ${DB_URL}")
 * e garante que a aplicação nunca suba silenciosamente sem banco configurado.
 */
@Component
public class RequiredDatabaseVariablesValidator implements BeanFactoryPostProcessor, EnvironmentAware {

    static final List<String> REQUIRED_VARIABLES = List.of("DB_URL", "DB_USERNAME", "DB_PASSWORD");

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        validate(environment);
    }

    static void validate(Environment environment) {
        List<String> missing = REQUIRED_VARIABLES.stream()
                .filter(name -> !StringUtils.hasText(environment.getProperty(name)))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Configuração do Oracle ausente: defina " + String.join(", ", missing)
                    + " como variáveis de ambiente ou no arquivo .env (veja .env.example).");
        }
    }
}
