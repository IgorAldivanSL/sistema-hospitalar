package com.igor.sistema_hospitalar.domain.entity;

import com.igor.sistema_hospitalar.domain.enums.TipoSanguineo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "prontuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", unique = true)
    private Paciente paciente;

    @Column(columnDefinition = "TEXT")
    private String historico;

    private String alergias;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo")
    private TipoSanguineo tipoSanguineo;
}
