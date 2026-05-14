package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.ProntuarioRequest;
import com.igor.sistema_hospitalar.api.dto.response.ProntuarioResponse;
import com.igor.sistema_hospitalar.domain.entity.Paciente;
import com.igor.sistema_hospitalar.domain.entity.Prontuario;
import com.igor.sistema_hospitalar.domain.enums.TipoSanguineo;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.PacienteRepository;
import com.igor.sistema_hospitalar.domain.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional
    public ProntuarioResponse create(ProntuarioRequest request) {
        Prontuario prontuario = new Prontuario();
        updateEntity(prontuario, request);
        prontuario = prontuarioRepository.save(prontuario);
        return toResponse(prontuario);
    }

    @Transactional
    public ProntuarioResponse update(Long id, ProntuarioRequest request) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com id: " + id));
        updateEntity(prontuario, request);
        prontuario = prontuarioRepository.save(prontuario);
        return toResponse(prontuario);
    }

    @Transactional(readOnly = true)
    public ProntuarioResponse findById(Long id) {
        return prontuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProntuarioResponse> findAll(Pageable pageable) {
        return prontuarioRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!prontuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prontuário não encontrado com id: " + id);
        }
        prontuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ProntuarioResponse> findByTipoSanguineo(TipoSanguineo tipoSanguineo, Pageable pageable) {
        return prontuarioRepository.findByTipoSanguineo(tipoSanguineo, pageable).map(this::toResponse);
    }

    private void updateEntity(Prontuario prontuario, ProntuarioRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado: " + request.getPacienteId()));
        prontuario.setPaciente(paciente);
        prontuario.setHistorico(request.getHistorico());
        prontuario.setAlergias(request.getAlergias());
        prontuario.setTipoSanguineo(request.getTipoSanguineo());
    }

    public ProntuarioResponse toResponse(Prontuario prontuario) {
        return ProntuarioResponse.builder()
                .id(prontuario.getId())
                .pacienteId(prontuario.getPaciente().getId())
                .pacienteNome(prontuario.getPaciente().getNome())
                .historico(prontuario.getHistorico())
                .alergias(prontuario.getAlergias())
                .tipoSanguineo(prontuario.getTipoSanguineo())
                .build();
    }
}
