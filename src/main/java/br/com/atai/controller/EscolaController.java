package br.com.atai.controller;

import br.com.atai.bo.EscolaBO;
import br.com.atai.dto.request.CreateEscolaRequest;
import br.com.atai.dto.request.UpdateEscolaRequest;
import br.com.atai.dto.response.EscolaResponse;
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
@RequestMapping("/api/escolas")
@Tag(name = "Escolas", description = "Cadastro e manutenção de escolas públicas")
public class EscolaController {

    private final EscolaBO escolaBO;

    public EscolaController(EscolaBO escolaBO) {
        this.escolaBO = escolaBO;
    }

    @GetMapping
    @Operation(summary = "Listar escolas")
    public ResponseEntity<List<EscolaResponse>> listar(
            @RequestParam(defaultValue = "false") boolean incluirInativas) {
        return ResponseEntity.ok(escolaBO.listar(incluirInativas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar escola por ID")
    public ResponseEntity<EscolaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(escolaBO.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar escola")
    public ResponseEntity<EscolaResponse> criar(@Valid @RequestBody CreateEscolaRequest request) {
        EscolaResponse response = escolaBO.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar escola")
    public ResponseEntity<EscolaResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateEscolaRequest request) {
        return ResponseEntity.ok(escolaBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar escola")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        escolaBO.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
