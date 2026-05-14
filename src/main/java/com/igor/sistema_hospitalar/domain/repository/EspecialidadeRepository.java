package com.igor.sistema_hospitalar.domain.repository;

import com.igor.sistema_hospitalar.domain.entity.Especialidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    Page<Especialidade> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
