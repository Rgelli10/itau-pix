package com.itau.pix.validator;

import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.dto.PixRequisicaoDto;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;

@Component
public class ValidadorCNPJ implements ValidadorPix{
    @Override
    public void validate(PixRequisicaoDto requisicao) {
        String valorChave = requisicao.getValorChave();
        if (!valorChave.matches("^\\d{14}$") || !isValidCnpj(valorChave)) {
            throw new ValidacaoException("Valor de CNPJ inválido.");
        }
    }

    private boolean isValidCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("[^\\d]", "");

        if (cnpj.length() != 14) {
            return false;
        }

        try {
            char dig13, dig14;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm += (num * peso);
                peso = peso == 9 ? 2 : peso + 1;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) {
                dig13 = '0';
            } else {
                dig13 = (char) ((11 - r) + 48);
            }

            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm += (num * peso);
                peso = peso == 9 ? 2 : peso + 1;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) {
                dig14 = '0';
            } else {
                dig14 = (char) ((11 - r) + 48);
            }

            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException e) {
            return false;
        }
    }
}
