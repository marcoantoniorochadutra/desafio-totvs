package com.totvs.conta.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthError {

    SENHA_INCORRETA("Senha incorreta."),
    SENHA_FRACA("A senha deve conter letras e números, entre %s e %s caracteres, podendo conter caracteres especiais."),
    EMAIL_NAO_ENCONTRADO("E-mail não encontrado."),
    USUARIO_DESATIVADO("Usuário inativo."),;

    private final String descricao;

}
