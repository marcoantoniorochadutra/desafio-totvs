package com.totvs.conta.interfaces.conta.controller;


import com.totvs.conta.application.service.ContaService;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.interfaces.conta.dto.ContaPagamentoDto;
import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.shared.dto.PaginacaoResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/conta")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public ContaDto cadastrarConta(@AuthenticationPrincipal LoginContextDto login,
                                   @RequestBody ContaDto contaDto) {
        return contaService.cadastrarConta(login, contaDto);
    }

    @PutMapping("/{id}")
    public ContaDto atualizarConta(@AuthenticationPrincipal LoginContextDto login,
                                   @PathVariable Long id,
                                   @RequestBody ContaDto contaDto) {
        return contaService.atualizarConta(login, id, contaDto);
    }

    @PatchMapping("/{id}/{situacao}")
    public ContaDto alterarSituacao(@AuthenticationPrincipal LoginContextDto login,
                                    @PathVariable Long id,
                                    @PathVariable Situacao situacao) {
        return contaService.alterarSituacaoConta(login, id, situacao);
    }

    @GetMapping("/{id}")
    public ContaDto obterContaPorId(@AuthenticationPrincipal LoginContextDto login,
                                    @PathVariable Long id) {
        return contaService.obterContaPorId(login, id);
    }

    @GetMapping
    public PaginacaoResultDto<ContaDto> obterContasParaPagar(
            @AuthenticationPrincipal LoginContextDto login,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoFinal,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false, defaultValue = "100") Integer totalPagina,
            @RequestParam(required = false, defaultValue = "0") Integer paginaAtual) {
        Map<String, Object> filtro = buildFiltroListagem(dataVencimentoInicial, dataVencimentoFinal,descricao);

        Pageable page = PageRequest.of(paginaAtual, totalPagina);
        return contaService.obterContasParaPagar(login, page, filtro);
    }

    @GetMapping("/total-pago")
    public ContaPagamentoDto obterValorTotalPago(
            @AuthenticationPrincipal LoginContextDto login,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        Map<String, Object> filtro = buildFiltroValorTotal(dataInicial, dataFinal);

        return contaService.obterValorTotalPagoPorPeriodo(login, filtro);
    }


    @PostMapping("/importar/csv")
    public List<ContaDto> importarContasCsv(@AuthenticationPrincipal LoginContextDto login, @RequestBody byte[] file) {
        return contaService.importarContasCsv(login, file);
    }

    private Map<String, Object> buildFiltroListagem(LocalDate dataVencimentoInicial, LocalDate dataVencimentoFinal, String descricao) {
        Map<String, Object> filtro = new HashMap<>();
        filtro.put("dataVencimentoInicial", dataVencimentoInicial);
        filtro.put("dataVencimentoFinal", dataVencimentoFinal);
        filtro.put("descricao", descricao);
        return filtro;
    }

    private Map<String, Object> buildFiltroValorTotal(LocalDate dataInicial, LocalDate dataFinal) {
        Map<String, Object> filtro = new HashMap<>();
        filtro.put("dataInicial", dataInicial);
        filtro.put("dataFinal", dataFinal);
        return filtro;
    }


}
