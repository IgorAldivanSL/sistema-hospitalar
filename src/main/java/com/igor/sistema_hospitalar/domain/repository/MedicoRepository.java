package com.igor.sistema_hospitalar.domain.repository;

import com.igor.sistema_hospitalar.domain.entity.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findByEspecialidadesId(Long especialidadeId, Pageable pageable);
}
