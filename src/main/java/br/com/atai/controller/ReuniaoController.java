package br.com.atai.controller;

import br.com.atai.bo.AtaBO;
import br.com.atai.bo.ReuniaoBO;
import br.com.atai.dto.request.AtaTextoRequest;
import br.com.atai.dto.request.CreateReuniaoRequest;
import br.com.atai.dto.request.UpdateReuniaoRequest;
import br.com.atai.dto.response.AtaResponse;
import br.com.atai.dto.response.ReuniaoResponse;
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
@RequestMapping("/api/reunioes")
@Tag(name = "Reuniões", description = "Registro de reuniões e vínculo com a ata")
public class ReuniaoController {

    private final ReuniaoBO reuniaoBO;
    private final AtaBO ataBO;

    public ReuniaoController(ReuniaoBO reuniaoBO, AtaBO ataBO) {
        this.reuniaoBO = reuniaoBO;
        this.ataBO = ataBO;
    }

    @GetMapping
    @Operation(summary = "Listar reuniões (filtráveis por escola e ano)")
    public ResponseEntity<List<ReuniaoResponse>> listar(
            @RequestParam(required = false) Long escolaId,
            @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(reuniaoBO.listar(escolaId, ano));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reunião por ID")
    public ResponseEntity<ReuniaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reuniaoBO.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nova reunião")
    public ResponseEntity<ReuniaoResponse> criar(@Valid @RequestBody CreateReuniaoRequest request) {
        ReuniaoResponse response = reuniaoBO.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar reunião (bloqueado se ata aprovada)")
    public ResponseEntity<ReuniaoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateReuniaoRequest request) {
        return ResponseEntity.ok(reuniaoBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir reunião (bloqueado se ata aprovada)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        reuniaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/ata")
    @Operation(summary = "Consultar ata da reunião")
    public ResponseEntity<AtaResponse> buscarAta(@PathVariable Long id) {
        return ResponseEntity.ok(ataBO.buscarPorReuniao(id));
    }

    @PostMapping("/{id}/ata")
    @Operation(summary = "Criar ata (status inicial MINUTA) para a reunião")
    public ResponseEntity<AtaResponse> criarAta(@PathVariable Long id,
                                                @Valid @RequestBody AtaTextoRequest request) {
        AtaResponse response = ataBO.criarParaReuniao(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/atas/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
