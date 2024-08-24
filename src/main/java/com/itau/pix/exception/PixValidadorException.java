package com.itau.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PixValidadorException extends ResponseStatusException {
    public PixValidadorException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}
