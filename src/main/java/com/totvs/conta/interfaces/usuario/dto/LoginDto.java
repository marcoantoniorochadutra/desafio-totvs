package com.totvs.conta.interfaces.usuario.dto;

import lombok.Builder;

@Builder
public record LoginDto(String email, String senha) {
}
