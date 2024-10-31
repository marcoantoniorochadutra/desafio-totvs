package com.totvs.conta.domain.model.conta;

import com.totvs.conta.shared.constants.MensagemErro;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Situacao {

    EM_ABERTO(0, 0, "Em aberto"),
    ATRASADO(1, 10, "Atrasado"),
    PAGO(2, 20, "Pago"),
    CANCELADO(3, 30, "Cancelado");


    private int ordinal;
    private int hierarquia;
    private String descricao;

    public static Situacao fromStr(String status) {
        return Arrays.stream(Situacao.values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(MensagemErro.Genericos.INFORMACAO_INVALIDA, status)));
    }

}
