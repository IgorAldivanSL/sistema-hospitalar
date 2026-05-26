package com.igor.sistema_hospitalar.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "api-keys", itemRelation = "api-key")
public class ApiKeyResponse extends RepresentationModel<ApiKeyResponse> {
    private Long id;
    private String keyValue;
    private String cliente;
    private boolean ativo;
    private LocalDateTime dataCriacao;
}
