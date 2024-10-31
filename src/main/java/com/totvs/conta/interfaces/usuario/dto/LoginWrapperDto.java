package com.totvs.conta.interfaces.usuario.dto;

import com.totvs.conta.shared.dto.LoginContextDto;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record LoginWrapperDto(String accessToken, String refreshToken, LoginContextDto loginContext) {
}
