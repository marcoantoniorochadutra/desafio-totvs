package com.totvs.conta.interfaces.usuario.controller;

import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import com.totvs.conta.infra.security.JwtProvider;
import com.totvs.conta.interfaces.usuario.dto.LoginDto;
import com.totvs.conta.interfaces.usuario.dto.LoginWrapperDto;
import com.totvs.conta.interfaces.usuario.dto.RegistroDto;
import com.totvs.conta.application.service.UsuarioService;
import com.totvs.conta.interfaces.usuario.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginWrapperDto login(@RequestBody LoginDto login) {
        return usuarioService.login(login);
    }

    @PostMapping("/register")
    public UsuarioDto register(@RequestBody RegistroDto registro) {
        return usuarioService.registrar(registro);
    }

    @GetMapping("/auth")
    public String auth() {

       return JwtProvider.generate(LoginContextDto.builder()
                .withEmail("mock_sql@email.com")
                .withId(10L)
                .withNivel(SelecionavelDto.builder()
                        .withChave("USUARIO")
                        .build())
                .build());
    }
}
