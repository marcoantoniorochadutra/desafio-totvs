package com.totvs.conta.interfaces.usuario.dto;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record UsuarioDto(Long id, String nome, String email, Boolean ativo){
}
