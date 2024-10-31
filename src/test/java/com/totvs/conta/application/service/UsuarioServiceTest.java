package com.totvs.conta.application.service;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.interfaces.usuario.dto.LoginDto;
import com.totvs.conta.interfaces.usuario.dto.LoginWrapperDto;
import com.totvs.conta.interfaces.usuario.dto.RegistroDto;
import com.totvs.conta.interfaces.usuario.dto.UsuarioDto;
import com.totvs.conta.application.exception.LoginError;
import com.totvs.conta.application.exception.LoginException;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import com.totvs.conta.infra.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UsuarioServiceTest extends TestSupport {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void deveCadastrarUsuario() {
        RegistroDto registro = RegistroDto.builder()
                .email("mock@email.com")
                .senha("senhaSegura")
                .nome("Mock")
                .build();

        UsuarioDto usuario = usuarioService.registrar(registro);

        assertNotNull(usuario.id());
        assertEquals("mock@email.com", usuario.email());
        assertTrue(usuario.ativo());
    }

    @Test
    public void deveLogarUsuario() {
        LoginDto registro = LoginDto.builder()
                .email("mock_sql@email.com")
                .senha("senhaSegura")
                .build();

        LoginWrapperDto usuario = usuarioService.login(registro);

        assertTrue(usuario.accessToken().startsWith("ey"));
        assertNotNull(usuario.refreshToken());
        assertEquals("mock_sql@email.com", usuario.loginContext().email());
    }

    @Test
    public void naoDeveLogarUsuarioEmailVazio() {
        LoginDto registro = LoginDto.builder()
                .email("")
                .senha("senha")
                .build();

        LoginException error = assertThrows(LoginException.class, () -> usuarioService.login(registro));

        assertEquals(LoginError.NOT_FOUND, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.EMAIL_NAO_ENCONTRADO, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioSenhaErrada() {
        LoginDto registro = LoginDto.builder()
                .email("mock_sql@email.com")
                .senha("senhaSeguraA")
                .build();

        LoginException error = assertThrows(LoginException.class, () -> usuarioService.login(registro));

        assertEquals(LoginError.WRONG_PASSWORD, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.SENHA_INCORRETA, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioNaoCadastrado() {
        LoginDto registro = LoginDto.builder()
                .email("mock_inexistente@email.com")
                .senha("senhaSegura")
                .build();

        LoginException error = assertThrows(LoginException.class, () -> usuarioService.login(registro));

        assertEquals(LoginError.NOT_FOUND, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.EMAIL_NAO_ENCONTRADO, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioDesativado() {
        LoginDto registro = LoginDto.builder()
                .email("disabled@email.com")
                .senha("senhaSegura")
                .build();

        LoginException error = assertThrows(LoginException.class, () -> usuarioService.login(registro));

        assertEquals(LoginError.DISABLED, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.USUARIO_DESATIVADO, error.getMessage());
    }
}
