package com.itau.pix.validator;

import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.dto.PixRequisicaoDto;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;

@Component
public class ValidadorCPF implements ValidadorPix{
    @Override
    public void validate(PixRequisicaoDto requisicao) {
        String valorChave = requisicao.getValorChave();
        if (!valorChave.matches("^\\d{11}$") || !isValidCpf(valorChave)) {
            throw new ValidacaoException("Valor de CPF inválido.");
        }
    }

    private boolean isValidCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            char dig10, dig11;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm += (num * peso);
                peso--;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm += (num * peso);
                peso--;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException e) {
            return false;
        }
    }
}
