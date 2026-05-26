package com.igor.sistema_hospitalar.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ApiKeyRequest {
    @NotBlank(message = "O nome do cliente/aplicação é obrigatório")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s]+$", message = "Nome deve conter apenas letras e números")
    private String cliente;
}
