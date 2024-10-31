package com.totvs.conta.domain.model.usuario;

import com.totvs.conta.domain.model.usuario.Usuario;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Usuario buscarPorId(Long id);

    Usuario buscarPorEmail(String email);

}
