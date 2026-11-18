package br.com.atai.controller;

import br.com.atai.bo.UsuarioBO;
import br.com.atai.dto.request.CreateUsuarioRequest;
import br.com.atai.dto.request.UpdateUsuarioRequest;
import br.com.atai.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários e perfis de acesso")
public class UsuarioController {

    private final UsuarioBO usuarioBO;

    public UsuarioController(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    @GetMapping
    @Operation(summary = "Listar usuários (opcionalmente por escola)")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(required = false) Long escolaId) {
        return ResponseEntity.ok(usuarioBO.listar(escolaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioBO.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CreateUsuarioRequest request) {
        UsuarioResponse response = usuarioBO.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioBO.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
