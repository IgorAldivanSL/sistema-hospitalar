package com.igor.sistema_hospitalar.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especialidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(unique = true)
    private String nome;

    private String descricao;

    @ManyToMany(mappedBy = "especialidades")
    @ToString.Exclude
    @Builder.Default
    private List<Medico> medicos = new ArrayList<>();
}
