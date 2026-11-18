package br.com.atai.dto.request;

import br.com.atai.model.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUsuarioRequest(
        Long escolaId,
        @NotBlank(message = "Nome do usuário é obrigatório.")
        @Size(max = 100, message = "Nome do usuário deve ter no máximo 100 caracteres.")
        String nome,
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 120, message = "E-mail deve ter no máximo 120 caracteres.")
        String email,
        @NotBlank(message = "Senha é obrigatória.")
        @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres.")
        String senhaHash,
        @NotNull(message = "Tipo de usuário é obrigatório.")
        TipoUsuario tipo
) {
}
