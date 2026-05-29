package com.igor.sistema_hospitalar.domain.repository;

import com.igor.sistema_hospitalar.domain.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
    Optional<Paciente> findByEmail(String email);
    Page<Paciente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
