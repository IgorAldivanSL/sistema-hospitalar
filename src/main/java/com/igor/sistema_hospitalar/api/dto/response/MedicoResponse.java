package com.igor.sistema_hospitalar.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "medicos", itemRelation = "medico")
public class MedicoResponse extends RepresentationModel<MedicoResponse> {
    private Long id;
    private String nome;
    private String crm;
    private String email;
    private String telefone;
    private List<EspecialidadeResponse> especialidades;
}
