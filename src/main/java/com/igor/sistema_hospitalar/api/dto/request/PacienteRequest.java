package com.igor.sistema_hospitalar.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @Past(message = "Data de nascimento deve ser uma data no passado")
    private LocalDate dataNascimento;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido, deve conter @")
    private String email;

    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Telefone deve conter apenas números")
    private String telefone;
}
