package com.totvs.conta.domain;

import com.totvs.conta.application.exception.LoginError;
import com.totvs.conta.application.exception.LoginException;
import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.domain.model.usuario.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsuarioDomainTest {

    @Test
    public void deveLancarExcecaoQuandoUsuarioDesativado() {
        LoginException ex = assertThrows(LoginException.class, () -> {
            Usuario usuario = Usuario.builder()
                    .withNome("Mock")
                    .withEmail("mock@email.com")
                    .withSenha("Senha")
                    .withAtivo(false)
                    .build();
            usuario.validarUsuario("Senha");
        });

        assertEquals(LoginError.DISABLED, ex.getLoginError());
        assertEquals(MensagemErro.Autenticacao.USUARIO_DESATIVADO, ex.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoSenhaErrada() {
        LoginException ex = assertThrows(LoginException.class, () -> {
            Usuario usuario = Usuario.builder()
                    .withNome("Mock")
                    .withEmail("mock@email.com")
                    .withSenha("Senha2")
                    .withAtivo(true)
                    .build();
            usuario.validarUsuario("Senha");
        });

        assertEquals(LoginError.WRONG_PASSWORD, ex.getLoginError());
        assertEquals(MensagemErro.Autenticacao.SENHA_INCORRETA, ex.getMessage());
    }
}
