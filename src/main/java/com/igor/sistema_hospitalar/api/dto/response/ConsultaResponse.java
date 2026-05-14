package com.igor.sistema_hospitalar.api.dto.response;

import com.igor.sistema_hospitalar.domain.enums.StatusConsulta;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "consultas", itemRelation = "consulta")
public class ConsultaResponse extends RepresentationModel<ConsultaResponse> {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;
    private LocalDateTime dataHora;
    private StatusConsulta status;
}
