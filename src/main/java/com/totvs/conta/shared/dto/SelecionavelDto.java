package com.totvs.conta.shared.dto;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record SelecionavelDto(String chave, String valor) {

}
