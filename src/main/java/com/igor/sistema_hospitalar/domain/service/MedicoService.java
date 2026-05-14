package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.MedicoRequest;
import com.igor.sistema_hospitalar.api.dto.response.MedicoResponse;
import com.igor.sistema_hospitalar.domain.entity.Especialidade;
import com.igor.sistema_hospitalar.domain.entity.Medico;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.EspecialidadeRepository;
import com.igor.sistema_hospitalar.domain.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final EspecialidadeService especialidadeService;

    @Transactional
    public MedicoResponse create(MedicoRequest request) {
        Medico medico = new Medico();
        updateEntity(medico, request);
        medico = medicoRepository.save(medico);
        return toResponse(medico);
    }

    @Transactional
    public MedicoResponse update(Long id, MedicoRequest request) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com id: " + id));
        updateEntity(medico, request);
        medico = medicoRepository.save(medico);
        return toResponse(medico);
    }

    @Transactional(readOnly = true)
    public MedicoResponse findById(Long id) {
        return medicoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<MedicoResponse> findAll(Pageable pageable) {
        return medicoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médico não encontrado com id: " + id);
        }
        medicoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<MedicoResponse> findByEspecialidade(Long especialidadeId, Pageable pageable) {
        return medicoRepository.findByEspecialidadesId(especialidadeId, pageable).map(this::toResponse);
    }

    private void updateEntity(Medico medico, MedicoRequest request) {
        medico.setNome(request.getNome());
        medico.setCrm(request.getCrm());
        medico.setEmail(request.getEmail());
        medico.setTelefone(request.getTelefone());
        if (request.getEspecialidadeIds() != null) {
            List<Especialidade> especialidades = especialidadeRepository.findAllById(request.getEspecialidadeIds());
            medico.setEspecialidades(especialidades);
        }
    }

    public MedicoResponse toResponse(Medico medico) {
        return MedicoResponse.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .email(medico.getEmail())
                .telefone(medico.getTelefone())
                .especialidades(medico.getEspecialidades() != null ? 
                    medico.getEspecialidades().stream().map(especialidadeService::toResponse).toList() : null)
                .build();
    }
}
