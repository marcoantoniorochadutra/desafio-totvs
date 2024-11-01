package com.totvs.conta.application.exception;

import lombok.Getter;


@Getter
public class AuthException extends RuntimeException {

    private final AuthError loginError;

    public AuthException(AuthError loginError, String mensagem) {
        super(mensagem);
        this.loginError = loginError;
    }
}
