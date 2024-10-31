package com.totvs.conta.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginError {

    WRONG_PASSWORD("Senha incorreta."),
    NOT_FOUND("E-mail não encontrado."),
    DISABLED("Usuário inativo."),;

    private final String descricao;

}
