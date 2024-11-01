package com.totvs.conta.application.service;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.interfaces.usuario.dto.LoginDto;
import com.totvs.conta.interfaces.usuario.dto.LoginWrapperDto;
import com.totvs.conta.interfaces.usuario.dto.RegistroDto;
import com.totvs.conta.interfaces.usuario.dto.UsuarioDto;
import com.totvs.conta.application.exception.AuthError;
import com.totvs.conta.application.exception.AuthException;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import com.totvs.conta.infra.support.TestSupport;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

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
                .senha("S3nha$egur@")
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
        assertEquals("mock_sql@email.com", usuario.loginContext().email());
    }

    @Test
    public void naoDeveLogarUsuarioEmailVazio() {
        LoginDto registro = LoginDto.builder()
                .email("")
                .senha("senha")
                .build();

        AuthException error = assertThrows(AuthException.class, () -> usuarioService.login(registro));

        assertEquals(AuthError.EMAIL_NAO_ENCONTRADO, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.EMAIL_NAO_ENCONTRADO, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioSenhaErrada() {
        LoginDto registro = LoginDto.builder()
                .email("mock_sql@email.com")
                .senha("senhaSeguraA")
                .build();

        AuthException error = assertThrows(AuthException.class, () -> usuarioService.login(registro));

        assertEquals(AuthError.SENHA_INCORRETA, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.SENHA_INCORRETA, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioNaoCadastrado() {
        LoginDto registro = LoginDto.builder()
                .email("mock_inexistente@email.com")
                .senha("senhaSegura")
                .build();

        AuthException error = assertThrows(AuthException.class, () -> usuarioService.login(registro));

        assertEquals(AuthError.EMAIL_NAO_ENCONTRADO, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.EMAIL_NAO_ENCONTRADO, error.getMessage());
    }

    @Test
    public void naoDeveLogarUsuarioDesativado() {
        LoginDto registro = LoginDto.builder()
                .email("disabled@email.com")
                .senha("senhaSegura")
                .build();

        AuthException error = assertThrows(AuthException.class, () -> usuarioService.login(registro));

        assertEquals(AuthError.USUARIO_DESATIVADO, error.getLoginError());
        assertEquals(MensagemErro.Autenticacao.USUARIO_DESATIVADO, error.getMessage());
    }

    @Test
    public void naoDeveCadastrarUsuarioSenhaFraca() {
        RegistroDto registro = RegistroDto.builder()
                .email("mock@email.com")
                .senha("senha")
                .nome("Mock")
                .build();

        AuthException error = assertThrows(AuthException.class, () -> usuarioService.registrar(registro));

        assertEquals(AuthError.SENHA_FRACA, error.getLoginError());
        assertEquals("A senha deve conter letras e números, entre 6 e 20 caracteres, podendo conter caracteres especiais.",
                error.getMessage());

    }

    @Test
    public void naoDeveCadastrarCamposInvalidos() {
        RegistroDto registro = RegistroDto.builder()
                .email("mockemailcom")
                .senha("S3nha$egur@")
                .nome(null)
                .build();

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> usuarioService.registrar(registro));
        String violations = ex.getConstraintViolations()
                .stream()
                .map(e -> e.getPropertyPath().toString())
                .sorted()
                .collect(Collectors.joining(","));

        assertEquals("email,nome", violations);
    }
}
