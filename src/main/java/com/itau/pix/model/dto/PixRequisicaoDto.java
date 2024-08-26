package com.itau.pix.model.dto;

import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.enums.TipoCorrentista;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PixRequisicaoDto {
    private String id;

    @NotNull(message = "Tipo de chave é obrigatório.")
    private TipoChave tipoChave;

    @NotBlank(message = "Valor da chave é obrigatório.")
    @Size(max = 77, message = "Valor da chave deve ter no máximo 77 caracteres.")
    private String valorChave;

    @NotBlank(message = "Tipo de conta é obrigatório.")
    @Pattern(regexp = "^(corrente|poupança)$", message = "Tipo de conta deve ser 'corrente' ou 'poupança'.")
    @Size(max = 10, message = "Tipo de conta deve ter no máximo 10 caracteres.")
    private String tipoConta;

    @NotBlank(message = "Número da agência é obrigatório.")
    @Pattern(regexp = "^\\d{4}$", message = "Número da agência deve ter exatamente 4 dígitos numéricos.")
    private String numeroAgencia;

    @NotBlank(message = "Número da conta é obrigatório.")
    @Pattern(regexp = "^\\d{8}$", message = "Número da conta deve ter exatamente 8 dígitos numéricos.")
    private String numeroConta;

    @NotBlank(message = "Nome do correntista é obrigatório.")
    @Size(max = 30, message = "Nome do correntista deve ter no máximo 30 caracteres.")
    private String nomeCorrentista;

    @Size(max = 45, message = "Sobrenome do correntista deve ter no máximo 45 caracteres.")
    private String sobrenomeCorrentista;

    @NotNull(message = "Tipo de correntista é obrigatório.")
    @Enumerated(EnumType.STRING)
    private TipoCorrentista tipoCorrentista;
}
