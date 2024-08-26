package com.itau.pix.validator;

import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.dto.PixRequisicaoDto;
import org.springframework.stereotype.Component;

@Component
public class ValidadorChaveAleatoria implements ValidadorPix {
    @Override
    public void validate(PixRequisicaoDto requisicao) {
        String valorChave = requisicao.getValorChave();
        if (!valorChave.matches("^[a-zA-Z0-9]{36}$")) {
            throw new ValidacaoException("Valor de chave aleatória inválido.");
        }
    }
}
