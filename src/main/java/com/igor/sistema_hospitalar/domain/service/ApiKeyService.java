package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.api.dto.request.ApiKeyRequest;
import com.igor.sistema_hospitalar.api.dto.response.ApiKeyResponse;
import com.igor.sistema_hospitalar.domain.entity.ApiKey;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyResponse create(ApiKeyRequest request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setCliente(request.getCliente());
        // keyValue e dataCriacao sao gerados no prePersist

        apiKey = apiKeyRepository.save(apiKey);
        return toResponse(apiKey);
    }

    @Transactional(readOnly = true)
    public Page<ApiKeyResponse> findAll(Pageable pageable) {
        return apiKeyRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Api Key não encontrada com o id: " + id));
        apiKeyRepository.delete(apiKey);
    }

    @Transactional(readOnly = true)
    public boolean isValid(String keyValue) {
        return apiKeyRepository.findByKeyValue(keyValue)
                .map(ApiKey::isAtivo)
                .orElse(false);
    }

    private ApiKeyResponse toResponse(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .keyValue(apiKey.getKeyValue())
                .cliente(apiKey.getCliente())
                .ativo(apiKey.isAtivo())
                .dataCriacao(apiKey.getDataCriacao())
                .build();
    }
}
