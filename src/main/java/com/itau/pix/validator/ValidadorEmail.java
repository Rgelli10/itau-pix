package com.itau.pix.validator;

import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.dto.PixRequisicaoDto;
import org.springframework.stereotype.Component;

@Component
public class ValidadorEmail implements ValidadorPix {

    @Override
    public void validate(PixRequisicaoDto requisicao) {
        String valorChave = requisicao.getValorChave();
        if (!valorChave.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") || valorChave.length() > 77) {
            throw new ValidacaoException("Valor de e-mail inválido.");
        }
    }
}
