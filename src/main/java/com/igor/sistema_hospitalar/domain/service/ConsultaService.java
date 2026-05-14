package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.ConsultaRequest;
import com.igor.sistema_hospitalar.api.dto.response.ConsultaResponse;
import com.igor.sistema_hospitalar.domain.entity.Consulta;
import com.igor.sistema_hospitalar.domain.entity.Medico;
import com.igor.sistema_hospitalar.domain.entity.Paciente;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.ConsultaRepository;
import com.igor.sistema_hospitalar.domain.repository.MedicoRepository;
import com.igor.sistema_hospitalar.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    @Transactional
    public ConsultaResponse create(ConsultaRequest request) {
        Consulta consulta = new Consulta();
        updateEntity(consulta, request);
        consulta = consultaRepository.save(consulta);
        return toResponse(consulta);
    }

    @Transactional
    public ConsultaResponse update(Long id, ConsultaRequest request) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
        updateEntity(consulta, request);
        consulta = consultaRepository.save(consulta);
        return toResponse(consulta);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse findById(Long id) {
        return consultaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> findAll(Pageable pageable) {
        return consultaRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consulta não encontrada com id: " + id);
        }
        consultaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> findByPaciente(Long pacienteId, Pageable pageable) {
        return consultaRepository.findByPacienteId(pacienteId, pageable).map(this::toResponse);
    }

    private void updateEntity(Consulta consulta, ConsultaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado: " + request.getPacienteId()));
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado: " + request.getMedicoId()));
        
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setDataHora(request.getDataHora());
        consulta.setStatus(request.getStatus());
    }

    public ConsultaResponse toResponse(Consulta consulta) {
        return ConsultaResponse.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPaciente().getId())
                .pacienteNome(consulta.getPaciente().getNome())
                .medicoId(consulta.getMedico().getId())
                .medicoNome(consulta.getMedico().getNome())
                .dataHora(consulta.getDataHora())
                .status(consulta.getStatus())
                .build();
    }
}
