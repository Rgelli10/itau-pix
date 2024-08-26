package com.itau.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GerenciadorException extends RuntimeException {
    @RestControllerAdvice
    public class GerenciadorExceptionHandler {
        @ExceptionHandler(ValidacaoException.class)
        public ResponseEntity<String> handleValidacao(ValidacaoException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
        }

        @ExceptionHandler(NaoEncontradoException.class)
        public ResponseEntity<String> handleNaoEncontrado(NaoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(NaoAutorizadoException.class)
        public ResponseEntity<String> handleNaoAutorizado(NaoAutorizadoException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

        @ExceptionHandler(RequisicaoInvalidaException.class)
        public ResponseEntity<String> handleRequisicaoInvalida(RequisicaoInvalidaException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleGeral(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }
}

