package com.totvs.conta.domain.model.usuario;

import com.totvs.conta.shared.constants.MensagemErro;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NivelUsuario {

    USUARIO(0, 10, "Usuário"),
    ADMIN(1, 10, "Usuário");

    private final int ordinal;
    private final int hierarquia;
    private final String descricao;

    public Boolean isUsuario() {
        return this.equals(USUARIO);
    }

    public static NivelUsuario fromStr(String status) {
        return Arrays.stream(NivelUsuario.values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(MensagemErro.Genericos.INFORMACAO_INVALIDA, status)));
    }

}
