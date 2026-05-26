package com.igor.sistema_hospitalar.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyValue;

    @Column(nullable = false)
    private String cliente; // Nome de quem possui a chave

    @Column(nullable = false)
    private boolean ativo;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (this.keyValue == null) {
            this.keyValue = UUID.randomUUID().toString();
        }
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
        this.ativo = true;
    }
}
