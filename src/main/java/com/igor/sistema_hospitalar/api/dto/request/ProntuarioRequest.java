package com.igor.sistema_hospitalar.api.dto.request;

import com.igor.sistema_hospitalar.domain.enums.TipoSanguineo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProntuarioRequest {
    @NotNull(message = "ID do Paciente é obrigatório")
    @Positive(message = "ID do Paciente deve ser inteiro positivo")
    private Long pacienteId;

    private String historico;

    private String alergias;

    private TipoSanguineo tipoSanguineo;
}
