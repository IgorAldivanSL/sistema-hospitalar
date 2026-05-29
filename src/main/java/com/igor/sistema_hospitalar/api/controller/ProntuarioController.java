package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.ProntuarioRequest;
import com.igor.sistema_hospitalar.api.dto.response.ProntuarioResponse;
import com.igor.sistema_hospitalar.domain.enums.TipoSanguineo;
import com.igor.sistema_hospitalar.domain.service.ProntuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/prontuarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Prontuários", description = "Endpoints para gerenciamento de prontuários médicos")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    @PostMapping
    @Operation(summary = "Criar novo prontuário", description = "Cadastra um novo prontuário no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prontuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ProntuarioResponse> create(@RequestBody @Valid ProntuarioRequest request) {
        ProntuarioResponse response = prontuarioService.create(request);
        addLinks(response);
        java.net.URI location = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar prontuário", description = "Atualiza os dados de um prontuário existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado")
    })
    public ResponseEntity<ProntuarioResponse> update(@PathVariable @Positive(message = "ID deve ser positivo") Long id, @RequestBody @Valid ProntuarioRequest request) {
        ProntuarioResponse response = prontuarioService.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar prontuário por ID", description = "Retorna um prontuário específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado")
    })
    public ResponseEntity<ProntuarioResponse> findById(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        ProntuarioResponse response = prontuarioService.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar prontuários", description = "Retorna uma lista paginada de todos os prontuários.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<ProntuarioResponse>>> findAll(@ParameterObject Pageable pageable, PagedResourcesAssembler<ProntuarioResponse> assembler) {
        Page<ProntuarioResponse> responses = prontuarioService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir prontuário", description = "Remove um prontuário do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prontuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        prontuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo-sanguineo/{tipo}")
    @Operation(summary = "Buscar prontuários por tipo sanguíneo", description = "Retorna uma lista paginada de prontuários que correspondem a um tipo sanguíneo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<ProntuarioResponse>>> findByTipoSanguineo(@PathVariable TipoSanguineo tipo, @ParameterObject Pageable pageable, PagedResourcesAssembler<ProntuarioResponse> assembler) {
        Page<ProntuarioResponse> responses = prontuarioService.findByTipoSanguineo(tipo, pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    private void addLinks(ProntuarioResponse response) {
        response.add(linkTo(methodOn(ProntuarioController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(ProntuarioController.class).findAll(Pageable.unpaged(), null)).withRel("prontuarios"));
        response.add(linkTo(methodOn(PacienteController.class).findById(response.getPacienteId())).withRel("paciente"));
    }
}
