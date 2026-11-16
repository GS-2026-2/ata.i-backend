package br.com.atai.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequiredDatabaseVariablesValidatorTest {

    @Test
    void shouldFailListingAllMissingVariables() {
        MockEnvironment environment = new MockEnvironment();

        assertThatThrownBy(() -> RequiredDatabaseVariablesValidator.validate(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB_URL")
                .hasMessageContaining("DB_USERNAME")
                .hasMessageContaining("DB_PASSWORD");
    }

    @Test
    void shouldTreatBlankValuesAsMissing() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DB_URL", "jdbc:oracle:thin:@//host:1521/service")
                .withProperty("DB_USERNAME", "user")
                .withProperty("DB_PASSWORD", "  ");

        assertThatThrownBy(() -> RequiredDatabaseVariablesValidator.validate(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB_PASSWORD")
                .hasMessageNotContaining("DB_URL");
    }

    @Test
    void shouldPassWhenAllVariablesArePresent() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DB_URL", "jdbc:oracle:thin:@//host:1521/service")
                .withProperty("DB_USERNAME", "user")
                .withProperty("DB_PASSWORD", "secret");

        assertThatCode(() -> RequiredDatabaseVariablesValidator.validate(environment)).doesNotThrowAnyException();
    }
}
