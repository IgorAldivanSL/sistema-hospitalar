package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.ConsultaRequest;
import com.igor.sistema_hospitalar.api.dto.response.ConsultaResponse;
import com.igor.sistema_hospitalar.domain.service.ConsultaService;
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
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Consultas", description = "Endpoints para gerenciamento de consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @Operation(summary = "Criar nova consulta", description = "Cria um novo agendamento de consulta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ConsultaResponse> create(@RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.create(request);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consulta", description = "Atualiza os dados de uma consulta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<ConsultaResponse> update(@PathVariable @Positive(message = "ID deve ser positivo") Long id, @RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID", description = "Retorna uma consulta específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<ConsultaResponse> findById(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        ConsultaResponse response = consultaService.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar consultas", description = "Retorna uma lista paginada de consultas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<ConsultaResponse>>> findAll(@ParameterObject Pageable pageable, PagedResourcesAssembler<ConsultaResponse> assembler) {
        Page<ConsultaResponse> responses = consultaService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir consulta", description = "Remove uma consulta do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consulta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        consultaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paciente/{idPaciente}")
    @Operation(summary = "Buscar consultas por paciente", description = "Retorna uma lista paginada de consultas de um paciente específico.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<ConsultaResponse>>> findByPaciente(@PathVariable @Positive(message = "ID deve ser positivo") Long idPaciente, @ParameterObject Pageable pageable, PagedResourcesAssembler<ConsultaResponse> assembler) {
        Page<ConsultaResponse> responses = consultaService.findByPaciente(idPaciente, pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    private void addLinks(ConsultaResponse response) {
        response.add(linkTo(methodOn(ConsultaController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(ConsultaController.class).findAll(Pageable.unpaged(), null)).withRel("consultas"));
        response.add(linkTo(methodOn(PacienteController.class).findById(response.getPacienteId())).withRel("paciente"));
        response.add(linkTo(methodOn(MedicoController.class).findById(response.getMedicoId())).withRel("medico"));
    }
}
