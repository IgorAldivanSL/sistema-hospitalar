package com.igor.sistema_hospitalar.api.dto.response;

import com.igor.sistema_hospitalar.domain.enums.TipoSanguineo;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "prontuarios", itemRelation = "prontuario")
public class ProntuarioResponse extends RepresentationModel<ProntuarioResponse> {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private String historico;
    private String alergias;
    private TipoSanguineo tipoSanguineo;
}
