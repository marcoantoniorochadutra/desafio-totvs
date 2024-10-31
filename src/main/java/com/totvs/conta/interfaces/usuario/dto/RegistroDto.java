package com.totvs.conta.interfaces.usuario.dto;

import lombok.Builder;

@Builder
public record RegistroDto(String email, String senha, String nome) {
}
