package com.totvs.conta.interfaces.conta.dto;

import com.totvs.conta.shared.dto.SelecionavelDto;
import lombok.Builder;

import java.time.LocalDate;


@Builder(setterPrefix = "with")
public record ContaDto(Long id, LocalDate dataPagamento, LocalDate dataVencimento, Double valor, String descricao, SelecionavelDto situacao, SelecionavelDto tipoConta, SelecionavelDto usuario) {
}
