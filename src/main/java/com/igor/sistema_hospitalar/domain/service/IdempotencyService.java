package com.igor.sistema_hospitalar.domain.service;

import com.igor.sistema_hospitalar.domain.entity.IdempotencyRecord;
import com.igor.sistema_hospitalar.domain.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> findByKey(String key) {
        return repository.findById(key);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRecord(String key, String requestHash, int responseStatus, String responseBody) {
        if (!repository.existsById(key)) {
            IdempotencyRecord record = IdempotencyRecord.builder()
                    .idempotencyKey(key)
                    .requestHash(requestHash)
                    .responseStatus(responseStatus)
                    .responseBody(responseBody)
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(record);
        }
    }

    public String generateHash(byte[] content) {
        if (content == null || content.length == 0) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(content);
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da requisição", e);
        }
    }
}
