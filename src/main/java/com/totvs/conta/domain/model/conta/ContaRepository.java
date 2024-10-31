package com.totvs.conta.domain.model.conta;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ContaRepository {

    Conta salvar(Conta conta);

    void apagar(Long id);

    Page<Conta> obterContasPagar(List<Situacao> situacao, String descricao, LocalDate dataInicial, LocalDate dataFinal, Long usuarioId, Pageable pageable);

    Conta buscarPorId(Long id);

    Double obterValorTotalPagarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal, Long usuarioId);
}
