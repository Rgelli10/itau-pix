package com.itau.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequisicaoInvalidaException extends ResponseStatusException {
    public RequisicaoInvalidaException(String motivo) {
        super(HttpStatus.BAD_REQUEST, motivo);
    }
}
