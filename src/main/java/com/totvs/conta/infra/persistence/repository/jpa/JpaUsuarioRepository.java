package com.totvs.conta.infra.persistence.repository.jpa;

import com.totvs.conta.domain.model.usuario.Usuario;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUsuarioRepository extends UsuarioRepository, JpaRepository<Usuario, Long> {

    default Usuario buscarPorId(Long id) {
        return getReferenceById(id);
    }

    default Usuario salvar(Usuario usuario) {
        return saveAndFlush(usuario);
    }

    Usuario findByEmail(String email);

    default Usuario buscarPorEmail(String email) {
        return findByEmail(email);
    }

}
