package com.igor.sistema_hospitalar.api.controller;

import com.igor.sistema_hospitalar.api.dto.request.ApiKeyRequest;
import com.igor.sistema_hospitalar.api.dto.response.ApiKeyResponse;
import com.igor.sistema_hospitalar.domain.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/auth/keys")
@RequiredArgsConstructor
@Validated
@Tag(name = "API Keys", description = "Endpoints para geração e gerenciamento de chaves de API")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @Operation(summary = "Gerar nova chave de API", description = "Gera uma nova chave de acesso para um cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chave gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ApiKeyResponse> create(@RequestBody @Valid ApiKeyRequest request) {
        ApiKeyResponse response = apiKeyService.create(request);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar chaves de API", description = "Retorna uma lista paginada de todas as chaves geradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @Parameters({
            @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
            @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação no formato campo,direção", example = "id,asc")
    })
    public ResponseEntity<PagedModel<EntityModel<ApiKeyResponse>>> findAll(
            @ParameterObject Pageable pageable,
            PagedResourcesAssembler<ApiKeyResponse> assembler) {

        Page<ApiKeyResponse> responses = apiKeyService.findAll(pageable);

        responses.forEach(this::addLinks);

        return ResponseEntity.ok(assembler.toModel(responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir chave de API", description = "Remove uma chave de API do sistema pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chave removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Chave não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        apiKeyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(ApiKeyResponse response) {
        response.add(linkTo(methodOn(ApiKeyController.class).findAll(Pageable.unpaged(), null)).withRel("api-keys"));
    }
}
