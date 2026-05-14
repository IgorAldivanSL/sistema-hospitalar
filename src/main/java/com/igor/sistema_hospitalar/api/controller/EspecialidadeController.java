package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.EspecialidadeRequest;
import com.igor.sistema_hospitalar.api.dto.response.EspecialidadeResponse;
import com.igor.sistema_hospitalar.domain.service.EspecialidadeService;
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
@RequestMapping("/api/v1/especialidades")
@RequiredArgsConstructor
@Validated
@Tag(name = "Especialidades", description = "Endpoints para gerenciamento de especialidades médicas")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @PostMapping
    @Operation(summary = "Criar nova especialidade", description = "Cadastra uma nova especialidade médica.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Especialidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<EspecialidadeResponse> create(@RequestBody @Valid EspecialidadeRequest request) {
        EspecialidadeResponse response = especialidadeService.create(request);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar especialidade", description = "Atualiza os dados de uma especialidade existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ResponseEntity<EspecialidadeResponse> update(@PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @RequestBody @Valid EspecialidadeRequest request) {
        EspecialidadeResponse response = especialidadeService.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar especialidade por ID", description = "Retorna uma especialidade específica pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ResponseEntity<EspecialidadeResponse> findById(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        EspecialidadeResponse response = especialidadeService.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar especialidades", description = "Retorna uma lista paginada de todas as especialidades.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<EspecialidadeResponse>>> findAll(@ParameterObject Pageable pageable,
            PagedResourcesAssembler<EspecialidadeResponse> assembler) {
        Page<EspecialidadeResponse> responses = especialidadeService.findAll(pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir especialidade", description = "Remove uma especialidade do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Especialidade removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        especialidadeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar especialidade por nome", description = "Pesquisa especialidades com base em parte do nome.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<EspecialidadeResponse>>> buscarPorNome(@RequestParam String nome,
            @ParameterObject Pageable pageable, PagedResourcesAssembler<EspecialidadeResponse> assembler) {
        Page<EspecialidadeResponse> responses = especialidadeService.buscarPorNome(nome, pageable);
        responses.forEach(this::addLinks);
        return ResponseEntity.ok(assembler.toModel(responses));
    }

    private void addLinks(EspecialidadeResponse response) {
        response.add(linkTo(methodOn(EspecialidadeController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(EspecialidadeController.class).findAll(Pageable.unpaged(), null))
                .withRel("especialidades"));
    }
}
