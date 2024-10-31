package com.totvs.conta.interfaces.handler;


import com.totvs.conta.application.exception.LoginException;
import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.shared.dto.MensagemDto;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@RestControllerAdvice
public class ManipuladorExcecoes extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> deveTratarErroConstraint(Exception ex, WebRequest request) {
        ex.getStackTrace();
        System.err.println("aaaa");
        System.err.println(ex.getCause());
        System.err.println(ex.getLocalizedMessage());
        System.err.println(ex.getClass());
        System.err.println(ex.getSuppressed());

        return ResponseEntity.badRequest().body(getMensagemDto(ex));
    }

    private MensagemDto getMensagemDto(Exception ex) {
        ex.getStackTrace();
        if (ex instanceof ConstraintViolationException) {
            return TratamentoConstraintException.tratar((ConstraintViolationException) ex);
        }

        if (ex instanceof DataIntegrityViolationException) {
            Da
            return TratamentoSqlException.tratar((SQLException) ex);
        }

        if (ex instanceof LoginException) {
            return new MensagemDto(tratarMensagemErroLogin((LoginException) ex));
        }

        if (ex instanceof JwtException) {
            return new MensagemDto(tratarMensagemErroJwt());
        }

        return new MensagemDto(MensagemErro.Genericos.PROBLEMA_SERVIDOR);
    }

    private String tratarMensagemErroLogin(LoginException ex) {
        return ex.getLoginError().getDescricao();
    }

    private String tratarMensagemErroJwt() {
        return MensagemErro.Autenticacao.ACESSO_NEGADO + StringUtils.SPACE + MensagemErro.Autenticacao.TOKEN_INVALIDO;
    }
}
