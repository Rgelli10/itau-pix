package com.itau.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NaoAutorizadoException extends ResponseStatusException {
    public NaoAutorizadoException(String motivo) {
        super(HttpStatus.UNAUTHORIZED, motivo);
    }
}
