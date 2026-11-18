package br.com.atai.controller;

import br.com.atai.bo.AtaBO;
import br.com.atai.dto.request.AlteracaoPrioridadeRequest;
import br.com.atai.dto.request.AtaTextoRequest;
import br.com.atai.dto.request.ConfirmarAtaRequest;
import br.com.atai.dto.response.AlteracaoPrioridadeResponse;
import br.com.atai.dto.response.AtaResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/atas")
@Tag(name = "Atas", description = "Edição de minutas, propostas de alteração de prioridades e confirmação pelo diretor")
public class AtaController {

    private final AtaBO ataBO;

    public AtaController(AtaBO ataBO) {
        this.ataBO = ataBO;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar ata e suas propostas de alteração")
    public ResponseEntity<AtaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ataBO.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar texto da ata (somente enquanto MINUTA)")
    public ResponseEntity<AtaResponse> atualizarTexto(@PathVariable Long id,
                                                      @Valid @RequestBody AtaTextoRequest request) {
        return ResponseEntity.ok(ataBO.atualizarTexto(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir ata (somente enquanto MINUTA)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        ataBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/alteracoes")
    @Operation(summary = "Listar propostas de alteração da ata")
    public ResponseEntity<List<AlteracaoPrioridadeResponse>> listarAlteracoes(@PathVariable Long id) {
        return ResponseEntity.ok(ataBO.listarAlteracoes(id));
    }

    @PostMapping("/{id}/alteracoes")
    @Operation(summary = "Adicionar proposta de alteração (somente enquanto MINUTA)")
    public ResponseEntity<AlteracaoPrioridadeResponse> adicionarAlteracao(
            @PathVariable Long id,
            @Valid @RequestBody AlteracaoPrioridadeRequest request) {
        AlteracaoPrioridadeResponse response = ataBO.adicionarAlteracao(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{alteracaoId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}/alteracoes/{alteracaoId}")
    @Operation(summary = "Editar proposta de alteração (somente enquanto MINUTA)")
    public ResponseEntity<AlteracaoPrioridadeResponse> atualizarAlteracao(
            @PathVariable Long id,
            @PathVariable Long alteracaoId,
            @Valid @RequestBody AlteracaoPrioridadeRequest request) {
        return ResponseEntity.ok(ataBO.atualizarAlteracao(id, alteracaoId, request));
    }

    @DeleteMapping("/{id}/alteracoes/{alteracaoId}")
    @Operation(summary = "Remover proposta de alteração (somente enquanto MINUTA)")
    public ResponseEntity<Void> removerAlteracao(@PathVariable Long id, @PathVariable Long alteracaoId) {
        ataBO.removerAlteracao(id, alteracaoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirmacao")
    @Operation(summary = "Confirmar ata (exclusivo do diretor) e aplicar alterações transacionalmente")
    public ResponseEntity<AtaResponse> confirmar(@PathVariable Long id,
                                                 @Valid @RequestBody ConfirmarAtaRequest request) {
        return ResponseEntity.ok(ataBO.confirmar(id, request));
    }
}
