package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.MedicoRequest;
import com.igor.sistema_hospitalar.api.dto.response.MedicoResponse;
import com.igor.sistema_hospitalar.domain.service.MedicoService;
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
@RequestMapping("/api/v1/medicos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Médicos", description = "Endpoints para gerenciamento de médicos")
public class MedicoController {

    private final MedicoService medicoService;

    @PostMapping
    @Operation(summary = "Criar novo médico", description = "Cadastra um novo médico no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Médico criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<MedicoResponse> create(@RequestBody @Valid MedicoRequest request) {
        MedicoResponse response = medicoService.create(request);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar médico", description = "Atualiza os dados de um médico existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Médico atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado")
    })
    public ResponseEntity<MedicoResponse> update(@PathVariable @Positive(message = "ID deve ser positivo") Long id, @RequestBody @Valid MedicoRequest request) {
        MedicoResponse response = medicoService.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID", description = "Retorna um médico específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Médico encontrado"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado")
    })
    public ResponseEntity<MedicoResponse> findById(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        MedicoResponse response = medicoService.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar médicos", description = "Retorna uma lista paginada de todos os médicos.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<MedicoResponse>>> findAll(@ParameterObject Pageable pageable, PagedResourcesAssembler<MedicoResponse> assembler) {
        Page<MedicoResponse> responses = medicoService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir médico", description = "Remove um médico do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Médico removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        medicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/especialidade/{idEspecialidade}")
    @Operation(summary = "Buscar médicos por especialidade", description = "Retorna uma lista paginada de médicos que possuem uma especialidade específica.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<MedicoResponse>>> findByEspecialidade(@PathVariable @Positive(message = "ID deve ser positivo") Long idEspecialidade, @ParameterObject Pageable pageable, PagedResourcesAssembler<MedicoResponse> assembler) {
        Page<MedicoResponse> responses = medicoService.findByEspecialidade(idEspecialidade, pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    private void addLinks(MedicoResponse response) {
        response.add(linkTo(methodOn(MedicoController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(MedicoController.class).findAll(Pageable.unpaged(), null)).withRel("medicos"));
    }
}
