package com.totvs.conta.interfaces.handler;


import com.totvs.conta.application.exception.AuthException;
import com.totvs.conta.domain.exception.DomainException;
import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.shared.dto.MensagemDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class ManipuladorExcecoes extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> tratarErro(Exception ex, WebRequest request) {
        log.error("ERRO GENÉRICO | OCORREU UM ERRO INESPERADO: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildMensagem(MensagemErro.Genericos.PROBLEMA_SERVIDOR));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> tratarErroDomain(DomainException ex, WebRequest request) {
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildMensagem(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> tratarErroConstraint(ConstraintViolationException ex, WebRequest request) {
        return FormatMsgErroConstraint.tratarExcecao(ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> tratarDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof SQLException sqlException) {
            return FormatMsgErroDb.tratarExcecao(sqlException);
        }

        return ResponseEntity.internalServerError()
                .body(buildMensagem(MensagemErro.Genericos.PROBLEMA_SERVIDOR));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> trataErroLogin(AuthException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildMensagem(ex.getMessage()));
    }

    private MensagemDto buildMensagem(String mensagem) {
        return new MensagemDto(mensagem);
    }

}
