package com.igor.sistema_hospitalar.api.dto.request;

import com.igor.sistema_hospitalar.domain.enums.StatusConsulta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequest {
    @NotNull(message = "ID do Paciente é obrigatório")
    @Positive(message = "ID do Paciente deve ser inteiro positivo")
    private Long pacienteId;

    @NotNull(message = "ID do Médico é obrigatório")
    @Positive(message = "ID do Médico deve ser inteiro positivo")
    private Long medicoId;

    @NotNull(message = "Data e Hora da consulta são obrigatórias")
    @Future(message = "A data deve ser no futuro")
    private LocalDateTime dataHora;

    @NotNull(message = "Status da consulta é obrigatório")
    private StatusConsulta status;
}
