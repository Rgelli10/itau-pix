package com.itau.pix.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ValidacaoException extends ResponseStatusException {

    private String detalhes;

    public ValidacaoException(String detalhes) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, detalhes);
    }
}

