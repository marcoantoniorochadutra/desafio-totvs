package com.totvs.conta.domain.model.conta;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TipoConta {

    GENERICA(0, "Conta genérica"),
    AGUA(1, "Conta de água"),
    LUZ(2, "Conta de energia elétrica"),
    INTERNET(3, "Conta de internet"),
    GAS(4, "Conta de gás"),
    COMIDA(5, "Despesas com alimentação"),
    SERVICOS(6, "Pagamentos por serviços"),
    ALUGUEL(7, "Pagamento de aluguel"),
    IMPOSTO(8, "Pagamento de impostos"),
    EMPRESTIMO(9, "Pagamento de empréstimos"),
    CARTAO_CREDITO(10, "Fatura de cartão de crédito"),
    DESPESAS_VIAGEM(11, "Despesas de viagem"),
    MERCADO(12, "Compras de mercado"),
    MECANICO(13, "Pagamentos de mecânico");

    private final int ordinal;
    private final String descricao;

    public static TipoConta fromStr(String chave) {
        return Arrays.stream(TipoConta.values())
                .filter(item -> item.name().equalsIgnoreCase(chave))
                .findAny()
                .orElse(GENERICA);
    }
}
