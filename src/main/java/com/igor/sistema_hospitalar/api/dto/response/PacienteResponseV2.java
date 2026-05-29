package com.igor.sistema_hospitalar.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "pacientes", itemRelation = "paciente")
public class PacienteResponseV2 extends RepresentationModel<PacienteResponseV2> {
    private Long id;
    private String nome;
    private String email;
    // V2 retorna um modelo simplificado sem dados sensíveis como CPF, Telefone e Data de Nascimento.
}
