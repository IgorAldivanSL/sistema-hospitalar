package com.igor.sistema_hospitalar.domain.repository;

import com.igor.sistema_hospitalar.domain.entity.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);
}
