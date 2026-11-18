package br.com.atai.controller;

import br.com.atai.bo.PrioridadeAnualBO;
import br.com.atai.dto.request.CreatePrioridadeRequest;
import br.com.atai.dto.request.UpdatePrioridadeRequest;
import br.com.atai.dto.response.HistoricoPrioridadeResponse;
import br.com.atai.dto.response.PrioridadeAnualResponse;
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
@RequestMapping("/api/prioridades")
@Tag(name = "Prioridades Anuais", description = "Lista anual de prioridades, gestão direta e histórico de decisões")
public class PrioridadeAnualController {

    private final PrioridadeAnualBO prioridadeBO;

    public PrioridadeAnualController(PrioridadeAnualBO prioridadeBO) {
        this.prioridadeBO = prioridadeBO;
    }

    @GetMapping
    @Operation(summary = "Listar prioridades vigentes (filtráveis por escola e ano)")
    public ResponseEntity<List<PrioridadeAnualResponse>> listar(
            @RequestParam(required = false) Long escolaId,
            @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(prioridadeBO.listarVigentes(escolaId, ano));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar prioridade anual por ID")
    public ResponseEntity<PrioridadeAnualResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prioridadeBO.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar prioridade anual manualmente")
    public ResponseEntity<PrioridadeAnualResponse> criar(@Valid @RequestBody CreatePrioridadeRequest request) {
        PrioridadeAnualResponse response = prioridadeBO.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar prioridade anual manualmente")
    public ResponseEntity<PrioridadeAnualResponse> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody UpdatePrioridadeRequest request) {
        return ResponseEntity.ok(prioridadeBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar prioridade anual")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        prioridadeBO.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Consultar histórico de alterações aprovadas da prioridade")
    public ResponseEntity<List<HistoricoPrioridadeResponse>> historico(@PathVariable Long id) {
        return ResponseEntity.ok(prioridadeBO.buscarHistorico(id));
    }
}
