package com.totvs.conta.application.service;

import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.PaginacaoResultDto;
import com.totvs.conta.interfaces.conta.dto.ContaPagamentoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ContaService {

    ContaDto cadastrarConta(LoginContextDto usuarioContext, ContaDto contaDto);

    ContaDto atualizarConta(LoginContextDto usuarioContext, Long id, ContaDto contaDto);

    ContaDto alterarSituacaoConta(LoginContextDto usuarioContext, Long id, Situacao situacao);

    ContaDto obterContaPorId(LoginContextDto usuarioContext, Long id);
    PaginacaoResultDto<ContaDto> obterContasParaPagar(LoginContextDto usuarioContext, Pageable pagelabe, Map<String, Object> filtro);

    ContaPagamentoDto obterValorTotalPagoPorPeriodo(LoginContextDto usuarioContext, Map<String, Object> filtro);

    List<ContaDto> importarContasCsv(LoginContextDto usuarioContext, byte[] bytes);
}
