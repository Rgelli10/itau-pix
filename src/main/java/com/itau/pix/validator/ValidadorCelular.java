package com.itau.pix.validator;

import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.dto.PixRequisicaoDto;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCelular implements ValidadorPix{
    @Override
    public void validate(PixRequisicaoDto requisicao) {
        String valorChave = requisicao.getValorChave();
        if (!valorChave.startsWith("+55")) {
            throw new ValidacaoException("O código do país deve ser +55 para números do Brasil.");
        }

        if (!valorChave.matches("^\\+55\\d{2}9\\d{8}$")) {
            throw new ValidacaoException("Número de celular inválido. O formato correto é +55DD9XXXXXXXX.");
        }
    }
}
