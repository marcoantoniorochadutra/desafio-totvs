package com.totvs.conta.shared.dto;

import java.util.List;


public record PaginacaoResultDto<T>(Integer paginaAtual, Integer totalPagina, Long quantidadeTotal, List<T> itens) {

}
