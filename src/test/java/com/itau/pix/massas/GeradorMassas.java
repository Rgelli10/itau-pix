package com.itau.pix.massas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeradorMassas {
    public static PixRequisicaoDto createPixKeyRequestDto() {
        PixRequisicaoDto dto = new PixRequisicaoDto();
        dto.setId("1234567891011");
        dto.setTipoChave(TipoChave.EMAIL);
        dto.setValorChave("test@example.com");
        dto.setTipoConta("corrente");
        dto.setNumeroAgencia("1234");
        dto.setNumeroConta("12345678");
        dto.setNomeCorrentista("João");
        dto.setSobrenomeCorrentista("Silva");
        dto.setPessoaFisica(true);
        dto.setPessoaJuridica(false);
        return dto;
    }

    public static List<PixModelo> createPixKeyList(int size) {
        List<PixModelo> pixKeyList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PixModelo pixKey = createPixKey();
            pixKey.setId(UUID.randomUUID().toString());
            pixKeyList.add(pixKey);
        }
        return pixKeyList;
    }

    public static PixAlterarRequisicaoDto createPixKeyUpdateRequestDto() {
        PixAlterarRequisicaoDto dto = new PixAlterarRequisicaoDto();
        dto.setId("1234567891011");
        dto.setTipoChave(TipoChave.CPF); // ou qualquer valor válido
        dto.setValorChave("123.456.789-00");
        dto.setTipoConta("poupança");
        dto.setNumeroAgencia("5678");
        dto.setNumeroConta("87654321");
        dto.setNomeCorrentista("Maria");
        dto.setSobrenomeCorrentista("Oliveira");
        return dto;
    }

    public static PixModelo createPixKey() {
        PixModelo pixKey = new PixModelo();
        pixKey.setId(UUID.randomUUID().toString());
        pixKey.setTipoChave(TipoChave.EMAIL); // ou qualquer valor válido
        pixKey.setValorChave("Teste@teste.com");
        pixKey.setTipoConta("corrente");
        pixKey.setNumeroAgencia("1234");
        pixKey.setNumeroConta("12345678");
        pixKey.setNomeCorrentista("Carlos");
        pixKey.setSobrenomeCorrentista("Pereira");
        pixKey.setDataHoraInclusao(LocalDateTime.now());
        pixKey.setInativa(false);
        pixKey.setPessoaFisica(true); // Se necessário, ajuste a lógica
        return pixKey;
    }

    public static PixModelo createDeactivatedPixKey() {
        PixModelo pixKey = createPixKey();
        pixKey.setInativa(true);
        pixKey.setDataHoraInativacao(LocalDateTime.now());
        return pixKey;
    }

    public static PixRequisicaoDto createPixKeyRequestDtoDuplucate() {
        PixRequisicaoDto dto = new PixRequisicaoDto();
        dto.setId(UUID.randomUUID().toString());
        dto.setTipoChave(TipoChave.EMAIL);
        dto.setValorChave("test@example.com");
        dto.setTipoConta("corrente");
        dto.setNumeroAgencia("1234");
        dto.setNumeroConta("12345678");
        dto.setNomeCorrentista("João");
        dto.setSobrenomeCorrentista("Silva");
        return dto;
    }

    public static PixRequisicaoDto createPixKeyInvalid() {
        PixRequisicaoDto pixKey = new PixRequisicaoDto();
        pixKey.setId(UUID.randomUUID().toString());
        pixKey.setTipoChave(null);
        pixKey.setValorChave("test@example.com");
        pixKey.setTipoConta("corrente");
        pixKey.setNumeroAgencia("1234");
        pixKey.setNumeroConta("12345678");
        pixKey.setNomeCorrentista("João");
        pixKey.setSobrenomeCorrentista("Silva");
//        pixKey.setDataHoraInclusao(LocalDateTime.now());
        return pixKey;
    }

    public static PixRequisicaoDto createInvalidPixKeyRequestDto() {
        PixRequisicaoDto dto = new PixRequisicaoDto();
        // Intencionalmente deixar campos obrigatórios nulos ou inválidos
        dto.setTipoChave(null); // Tipo de chave obrigatório
        dto.setValorChave(""); // Valor da chave obrigatório e não pode ser vazio
        dto.setTipoConta(""); // Tipo de conta obrigatório e não pode ser vazio
        dto.setNumeroAgencia("123"); // Número da agência deve ter exatamente 4 dígitos
        dto.setNumeroConta("12345"); // Número da conta deve ter exatamente 8 dígitos
        dto.setNomeCorrentista(""); // Nome do correntista obrigatório e não pode ser vazio
        dto.setSobrenomeCorrentista("Silva"); // Sobrenome opcional
        return dto;
    }

    public static PixRequisicaoDto createDuplicatedPixKeyRequestDto() {
        PixRequisicaoDto dto = createPixKeyRequestDto();
        // Ajustar o valor da chave para que seja considerado duplicado
        dto.setValorChave("duplicated@example.com");
        return dto;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
