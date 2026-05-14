package com.igor.sistema_hospitalar.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicoRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras")
    private String nome;

    @NotBlank(message = "CRM é obrigatório")
    private String crm;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido, deve conter @")
    private String email;

    @Pattern(regexp = "^\\d+$", message = "Telefone deve conter apenas números")
    private String telefone;

    private List<Long> especialidadeIds;
}
