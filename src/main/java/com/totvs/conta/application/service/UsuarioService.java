package com.totvs.conta.application.service;

import com.totvs.conta.interfaces.usuario.dto.LoginDto;
import com.totvs.conta.interfaces.usuario.dto.LoginWrapperDto;
import com.totvs.conta.interfaces.usuario.dto.RegistroDto;
import com.totvs.conta.interfaces.usuario.dto.UsuarioDto;

public interface UsuarioService {

    LoginWrapperDto login(LoginDto login);

    UsuarioDto registrar(RegistroDto registro);

}
