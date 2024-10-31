package com.totvs.conta.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class LoginException extends RuntimeException {

    private final LoginError loginError;

    public LoginException(LoginError loginError, String mensagem) {
        super(mensagem);
        this.loginError = loginError;
    }
}
