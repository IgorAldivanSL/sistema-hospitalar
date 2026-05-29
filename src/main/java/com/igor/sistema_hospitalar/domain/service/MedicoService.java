package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.MedicoRequest;
import com.igor.sistema_hospitalar.api.dto.response.MedicoResponse;
import com.igor.sistema_hospitalar.domain.entity.Especialidade;
import com.igor.sistema_hospitalar.domain.entity.Medico;
import com.igor.sistema_hospitalar.domain.exception.BusinessException;
import com.igor.sistema_hospitalar.domain.exception.ResourceConflictException;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.EspecialidadeRepository;
import com.igor.sistema_hospitalar.domain.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final EspecialidadeService especialidadeService;

    @Transactional
    public MedicoResponse create(MedicoRequest request) {
        checkDuplication(null, request.getCrm(), request.getEmail());
        Medico medico = new Medico();
        updateEntity(medico, request);
        medico = medicoRepository.save(medico);
        return toResponse(medico);
    }

    @Transactional
    public MedicoResponse update(Long id, MedicoRequest request) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com id: " + id));
        checkDuplication(id, request.getCrm(), request.getEmail());
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

    private void checkDuplication(Long id, String crm, String email) {
        Optional<Medico> byCrm = medicoRepository.findByCrm(crm);
        if (byCrm.isPresent() && !byCrm.get().getId().equals(id)) {
            throw new ResourceConflictException("CRM já cadastrado");
        }
        Optional<Medico> byEmail = medicoRepository.findByEmail(email);
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            throw new ResourceConflictException("Email já cadastrado");
        }
    }

    private void updateEntity(Medico medico, MedicoRequest request) {
        medico.setNome(request.getNome());
        medico.setCrm(request.getCrm());
        medico.setEmail(request.getEmail());
        medico.setTelefone(request.getTelefone());
        if (request.getEspecialidadeIds() != null && !request.getEspecialidadeIds().isEmpty()) {
            List<Especialidade> especialidades = especialidadeRepository.findAllById(request.getEspecialidadeIds());
            if (especialidades.size() != request.getEspecialidadeIds().size()) {
                throw new BusinessException("Uma ou mais especialidades informadas não existem.");
            }
            medico.setEspecialidades(especialidades);
        } else {
            medico.setEspecialidades(null);
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
