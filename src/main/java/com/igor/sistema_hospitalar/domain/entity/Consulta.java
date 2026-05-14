package com.igor.sistema_hospitalar.domain.entity;

import com.igor.sistema_hospitalar.domain.enums.StatusConsulta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @NotNull(message = "Médico é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @NotNull(message = "Data e Hora da consulta são obrigatórias")
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @NotNull(message = "Status da consulta é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusConsulta status;
}
