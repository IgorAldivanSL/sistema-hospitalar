package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.EspecialidadeRequest;
import com.igor.sistema_hospitalar.api.dto.response.EspecialidadeResponse;
import com.igor.sistema_hospitalar.domain.entity.Especialidade;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    @Transactional
    public EspecialidadeResponse create(EspecialidadeRequest request) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(request.getNome());
        especialidade.setDescricao(request.getDescricao());
        especialidade = especialidadeRepository.save(especialidade);
        return toResponse(especialidade);
    }

    @Transactional
    public EspecialidadeResponse update(Long id, EspecialidadeRequest request) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada com id: " + id));
        especialidade.setNome(request.getNome());
        especialidade.setDescricao(request.getDescricao());
        especialidade = especialidadeRepository.save(especialidade);
        return toResponse(especialidade);
    }

    @Transactional(readOnly = true)
    public EspecialidadeResponse findById(Long id) {
        return especialidadeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<EspecialidadeResponse> findAll(Pageable pageable) {
        return especialidadeRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especialidade não encontrada com id: " + id);
        }
        especialidadeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<EspecialidadeResponse> buscarPorNome(String nome, Pageable pageable) {
        return especialidadeRepository.findByNomeContainingIgnoreCase(nome, pageable).map(this::toResponse);
    }

    public EspecialidadeResponse toResponse(Especialidade especialidade) {
        return EspecialidadeResponse.builder()
                .id(especialidade.getId())
                .nome(especialidade.getNome())
                .descricao(especialidade.getDescricao())
                .build();
    }
}
