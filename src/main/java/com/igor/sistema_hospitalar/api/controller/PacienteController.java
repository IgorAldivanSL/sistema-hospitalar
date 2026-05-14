package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.PacienteRequest;
import com.igor.sistema_hospitalar.api.dto.response.PacienteResponse;
import com.igor.sistema_hospitalar.domain.service.PacienteService;
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
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pacientes", description = "Endpoints para gerenciamento de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @Operation(summary = "Criar novo paciente", description = "Cria um novo registro de paciente no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<PacienteResponse> create(@RequestBody @Valid PacienteRequest request) {
        PacienteResponse response = pacienteService.create(request);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar paciente", description = "Atualiza os dados de um paciente existente pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<PacienteResponse> update(@PathVariable @Positive(message = "ID deve ser positivo") Long id, @RequestBody @Valid PacienteRequest request) {
        PacienteResponse response = pacienteService.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID", description = "Retorna um paciente especifico baseado no seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<PacienteResponse> findById(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        PacienteResponse response = pacienteService.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping(headers = "X-API-Version=1")
    @Operation(summary = "Listar todos os pacientes (V1)", description = "Retorna uma lista paginada de todos os pacientes.")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<PacienteResponse>>> findAll(@ParameterObject Pageable pageable, PagedResourcesAssembler<PacienteResponse> assembler) {
        Page<PacienteResponse> responses = pacienteService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @GetMapping(headers = "X-API-Version=2")
    @Operation(summary = "Listar todos os pacientes (V2)", description = "Retorna uma lista paginada de todos os pacientes com status 206.")
    @ApiResponse(responseCode = "206", description = "Lista de pacientes retornada parcialmente")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<PacienteResponse>>> findAllV2(@ParameterObject Pageable pageable, PagedResourcesAssembler<PacienteResponse> assembler) {
        Page<PacienteResponse> responses = pacienteService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir paciente", description = "Remove um paciente do sistema pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paciente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar paciente por CPF", description = "Retorna um paciente especifico baseado no seu CPF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<PacienteResponse> findByCpf(@PathVariable String cpf) {
        PacienteResponse response = pacienteService.findByCpf(cpf);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    private void addLinks(PacienteResponse response) {
        response.add(linkTo(methodOn(PacienteController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(PacienteController.class).findAll(Pageable.unpaged(), null)).withRel("pacientes"));
    }
}
