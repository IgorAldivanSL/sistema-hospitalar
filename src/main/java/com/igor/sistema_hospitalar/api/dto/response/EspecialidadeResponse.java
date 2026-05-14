package com.igor.sistema_hospitalar.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "especialidades", itemRelation = "especialidade")
public class EspecialidadeResponse extends RepresentationModel<EspecialidadeResponse> {
    private Long id;
    private String nome;
    private String descricao;
}
