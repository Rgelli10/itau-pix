package com.itau.pix.validator;

import com.itau.pix.model.dto.PixRequisicaoDto;

public interface ValidadorPix {
    void validate(PixRequisicaoDto requisicao);
}
