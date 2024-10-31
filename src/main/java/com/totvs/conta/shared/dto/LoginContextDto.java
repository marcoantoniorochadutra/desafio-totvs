package com.totvs.conta.shared.dto;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record LoginContextDto(Long id, String email, SelecionavelDto nivel) {
}
