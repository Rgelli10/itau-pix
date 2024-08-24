package com.itau.pix.model.dto;

import com.itau.pix.model.TipoChave;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
public class PixKeyRequestDto {
    private String id;

    @NotNull(message = "Tipo de chave é obrigatório.")
    private TipoChave tipoChave; // Tipo de chave (celular|email|cpf|cnpj|aleatorio)

    @NotBlank(message = "Valor da chave é obrigatório.")
    @Size(max = 77, message = "Valor da chave deve ter no máximo 77 caracteres.")
    private String valorChave; // Valor da chave (email, CPF, etc.)

    @NotBlank(message = "Tipo de conta é obrigatório.")
    @Pattern(regexp = "^(corrente|poupança)$", message = "Tipo de conta deve ser 'corrente' ou 'poupança'.")
    @Size(max = 10, message = "Tipo de conta deve ter no máximo 10 caracteres.")
    private String tipoConta; // Tipo da conta (corrente|poupança)

    @NotBlank(message = "Número da agência é obrigatório.")
    @Pattern(regexp = "^\\d{4}$", message = "Número da agência deve ter exatamente 4 dígitos numéricos.")
    private String numeroAgencia; // Número da agência (4 dígitos numéricos)

    @NotBlank(message = "Número da conta é obrigatório.")
    @Pattern(regexp = "^\\d{8}$", message = "Número da conta deve ter exatamente 8 dígitos numéricos.")
    private String numeroConta; // Número da conta (8 dígitos numéricos)

    @NotBlank(message = "Nome do correntista é obrigatório.")
    @Size(max = 30, message = "Nome do correntista deve ter no máximo 30 caracteres.")
    private String nomeCorrentista; // Nome do correntista

    @Size(max = 45, message = "Sobrenome do correntista deve ter no máximo 45 caracteres.")
    private String sobrenomeCorrentista; // Sobrenome do correntista (opcional)

    private boolean pessoaFisica;
    private boolean pessoaJuridica;

}
