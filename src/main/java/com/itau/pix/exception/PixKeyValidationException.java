package com.itau.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PixKeyValidationException extends ResponseStatusException {
    public PixKeyValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}
