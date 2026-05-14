package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.PacienteRequest;
import com.igor.sistema_hospitalar.api.dto.response.PacienteResponse;
import com.igor.sistema_hospitalar.domain.entity.Paciente;
import com.igor.sistema_hospitalar.domain.exception.BusinessException;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional
    public PacienteResponse create(PacienteRequest request) {
        if (pacienteRepository.findByCpf(request.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado");
        }
        Paciente paciente = new Paciente();
        updateEntity(paciente, request);
        paciente = pacienteRepository.save(paciente);
        return toResponse(paciente);
    }

    @Transactional
    public PacienteResponse update(Long id, PacienteRequest request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + id));
        updateEntity(paciente, request);
        paciente = pacienteRepository.save(paciente);
        return toResponse(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteResponse findById(Long id) {
        return pacienteRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponse> findAll(Pageable pageable) {
        return pacienteRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente não encontrado com id: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PacienteResponse findByCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com CPF: " + cpf));
    }

    private void updateEntity(Paciente paciente, PacienteRequest request) {
        paciente.setNome(request.getNome());
        paciente.setCpf(request.getCpf());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setEmail(request.getEmail());
        paciente.setTelefone(request.getTelefone());
    }

    public PacienteResponse toResponse(Paciente paciente) {
        return PacienteResponse.builder()
                .id(paciente.getId())
                .nome(paciente.getNome())
                .cpf(paciente.getCpf())
                .dataNascimento(paciente.getDataNascimento())
                .email(paciente.getEmail())
                .telefone(paciente.getTelefone())
                .build();
    }
}
