package com.totvs.conta.application.service;

import com.totvs.conta.domain.model.conta.TipoConta;
import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.domain.exception.DomainException;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.infra.support.TestSupport;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.PaginacaoResultDto;
import com.totvs.conta.interfaces.conta.dto.ContaPagamentoDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContaServiceTest extends TestSupport {

    @Autowired
    private ContaService contaService;

    @Test
    public void deveCadastrarConta() {
        ContaDto contaRequest = ContaDto.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(4500.0)
                .withDescricao("Injeção Eletronica")
                .withTipoConta(buildSelecionavelDto(TipoConta.MECANICO.name(), TipoConta.MECANICO.getDescricao()))
                .withSituacao(buildSelecionavelDto(Situacao.EM_ABERTO.name(), Situacao.EM_ABERTO.getDescricao()))
                .withUsuario(buildSelecionavelDto("9"))
                .build();

        ContaDto contaResponse = contaService.cadastrarConta(loginAdmin(), contaRequest);

        assertNotNull(contaResponse.id());
        assertNull(contaResponse.dataPagamento());
        assertEquals(4500.0, contaResponse.valor());
        assertEquals("Injeção Eletronica", contaResponse.descricao());
        assertEquals(Situacao.EM_ABERTO.name(), contaResponse.situacao().chave());
        assertEquals(LocalDate.now().plusDays(7), contaResponse.dataVencimento());
        assertEquals("9", contaResponse.usuario().chave());
        assertEquals("admin", contaResponse.usuario().valor());
    }

    @Test
    public void deveAtualizarConta() {

        ContaDto contaRequest = ContaDto.builder()
                .withDataVencimento(LocalDate.now().plusDays(30))
                .withValor(7500.0)
                .withDescricao("Injeção Eletronica - Atualizada")
                .withTipoConta(buildSelecionavelDto(TipoConta.MECANICO.name(), TipoConta.MECANICO.getDescricao()))
                .withSituacao(buildSelecionavelDto(Situacao.EM_ABERTO.name(), Situacao.EM_ABERTO.getDescricao()))
                .withUsuario(buildSelecionavelDto("9"))
                .build();

        ContaDto contaAtualizada = contaService.atualizarConta(loginAdmin(), 150L, contaRequest);

        assertNotNull(contaAtualizada.id());
        assertNull(contaAtualizada.dataPagamento());
        assertEquals(7500.0, contaAtualizada.valor());
        assertEquals("Injeção Eletronica - Atualizada", contaAtualizada.descricao());
        assertEquals(Situacao.EM_ABERTO.name(), contaAtualizada.situacao().chave());
        assertEquals(LocalDate.now().plusDays(30), contaAtualizada.dataVencimento());
        assertEquals("9", contaAtualizada.usuario().chave());
        assertEquals("admin", contaAtualizada.usuario().valor());
    }

    @Test
    public void deveAtualizarSituacaoConta() {
        LocalDate hoje = LocalDate.now();

        ContaDto contaAtualizada = contaService.alterarSituacaoConta(login(), 160L, Situacao.PAGO);

        assertEquals(160L, contaAtualizada.id());
        assertEquals(Situacao.PAGO.name(), contaAtualizada.situacao().chave());
        assertEquals(250.00, contaAtualizada.valor());
        assertEquals("Conta de Internet", contaAtualizada.descricao());
        assertEquals(hoje, contaAtualizada.dataVencimento());
        assertEquals(hoje, contaAtualizada.dataPagamento());
    }

    @Test
    public void deveListarContasParaPagar() {

        criarConta(usuarioAdmin(), Situacao.EM_ABERTO, 1500.0, "Pagamento Caixa");
        criarConta(usuarioAdmin(), Situacao.ATRASADO, 2500.0, "Pagamento João");
        criarConta(usuarioAdmin(), Situacao.PAGO, 4500.0, "Pagamento João", LocalDate.now());

        Map<String, Object> filtros = new HashMap<>();
        filtros.put("usuarioId", 9L);
        filtros.put("descricao", "Pagamento");
        filtros.put("dataVencimentoInicial", LocalDate.now());
        filtros.put("dataVencimentoFinal", LocalDate.now().plusDays(30));
        Pageable page = PageRequest.of(0, 10);

        PaginacaoResultDto<ContaDto> contaAtualizada = contaService.obterContasParaPagar(loginAdmin(), page, filtros);
        assertEquals(0, contaAtualizada.paginaAtual());
        assertEquals(1, contaAtualizada.totalPagina());
        assertEquals(2, contaAtualizada.quantidadeTotal());

        List<ContaDto> contas = contaAtualizada.itens();

        assertEquals(2, contas.size());

        assertEquals(1500.0, contas.get(0).valor());
        assertEquals("Pagamento Caixa", contas.get(0).descricao());

        assertEquals(2500.0, contas.get(1).valor());
        assertEquals("Pagamento João", contas.get(1).descricao());

    }

    @Test
    public void deveListarContasParaPagarSemDescricao() {

        criarConta(usuario(), Situacao.EM_ABERTO, 6500.0, "Compra FT");
        criarConta(usuario(), Situacao.ATRASADO, 2500.0, "Pagamento Claudio");
        criarConta(usuario(), Situacao.PAGO, 4500.0, "Pagamento João", LocalDate.now());

        Map<String, Object> filtros = new HashMap<>();
        filtros.put("usuarioId", 9L);
        filtros.put("dataVencimentoInicial", LocalDate.now());
        filtros.put("dataVencimentoFinal", LocalDate.now().plusDays(30));
        Pageable page = PageRequest.of(0, 10);

        PaginacaoResultDto<ContaDto> contaAtualizada = contaService.obterContasParaPagar(login(), page, filtros);

        assertEquals(0, contaAtualizada.paginaAtual());
        assertEquals(1, contaAtualizada.totalPagina());
        assertEquals(4, contaAtualizada.quantidadeTotal());

        List<ContaDto> contas = contaAtualizada.itens();

        assertEquals(4, contas.size());

        assertEquals(250.0, contas.get(0).valor());
        assertEquals("Conta de Internet", contas.get(0).descricao());

        assertEquals(550.0, contas.get(1).valor());
        assertEquals("Pagamento de Mercado", contas.get(1).descricao());

        assertEquals(6500.0, contas.get(2).valor());
        assertEquals("Compra FT", contas.get(2).descricao());

        assertEquals(2500.0, contas.get(3).valor());
        assertEquals("Pagamento Claudio", contas.get(3).descricao());
    }

    @Test
    public void deveBuscarTotalPagoNoPeriodo() {

        LocalDate dataBase = LocalDate.now().plusDays(50);

        criarConta(usuarioAdmin(), Situacao.PAGO, 4500.0, "Pagamento ao Mercado João", dataBase);
        criarConta(usuarioAdmin(), Situacao.PAGO, 2500.0, "Pagamento ao Mercado Cleber", dataBase);
        criarConta(usuarioAdmin(), Situacao.ATRASADO, 1500.0, "Pagamento ao Mercado Batata");
        criarConta(usuarioAdmin(), Situacao.EM_ABERTO, 1500.0, "Pagamento ao Mercado Batata");
        criarConta(usuarioAdmin(), Situacao.CANCELADO, 1500.0, "Pagamento ao Mercado Batata", dataBase);
        criarConta(usuarioAdmin(), Situacao.PAGO, 100.0, "Pagamento ao Mercado Gustavo", dataBase);


        Map<String, Object> filtros = new HashMap<>();
        filtros.put("usuarioId", 9L);
        filtros.put("dataInicial", dataBase.minusDays(1));
        filtros.put("dataFinal", dataBase.plusDays(2));


        ContaPagamentoDto contaPagamento = contaService.obterValorTotalPagoPorPeriodo(loginAdmin(), filtros);

        assertEquals(7100.0, contaPagamento.valorTotal());

    }

    @Test
    public void deveBuscarContaPorIdUsuarioAdmin() {

        ContaDto contaPagamento = contaService.obterContaPorId(loginAdmin(), 190L);

        assertEquals(190L, contaPagamento.id());
        assertEquals(550.0, contaPagamento.valor());
        assertEquals("Pagamento de Mercado", contaPagamento.descricao());

    }

    @Test
    public void naoDeveBuscarContaDiferentesUsuarios() {
        DomainException ex = assertThrows(DomainException.class, () -> contaService.obterContaPorId(login(), 170L));
        assertEquals(MensagemErro.Genericos.USUARIO_NAO_AUTORIZADO, ex.getMessage());
    }


    @Test
    public void deveImportarDoCsv() throws IOException {
        byte[] csvFile = Files.readAllBytes(Paths.get("src/test/resources/example.csv"));

        List<ContaDto> contas = contaService.importarContasCsv(loginAdmin(), csvFile);

        assertEquals(3, contas.size());

        ContaDto conta = contas.getFirst();
        assertNotNull(conta.id());
        assertEquals(150.0, conta.valor());
        assertEquals("Compra de materiais", conta.descricao());
        assertEquals("PAGO", conta.situacao().chave());

    }

    @Test
    public void naoDeveCadastrarContaCamposVazios() {

        ContaDto contaRequest = ContaDto.builder()
                .withValor(100.0)
                .withUsuario(buildSelecionavelDto("9"))
                .build();

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> contaService.cadastrarConta(loginAdmin(), contaRequest));
        String violations = ex.getConstraintViolations()
                .stream()
                .map(e -> e.getPropertyPath().toString())
                .sorted()
                .collect(Collectors.joining(","));
        assertEquals("dataVencimento,descricao", violations);
    }

    @Test
    public void naoDeveCadastrarContaValorInvalido() {

        ContaDto contaRequest = ContaDto.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withDescricao("Injeção Eletronica")
                .withTipoConta(buildSelecionavelDto(TipoConta.MECANICO.name(), TipoConta.MECANICO.getDescricao()))
                .withSituacao(buildSelecionavelDto(Situacao.EM_ABERTO.name(), Situacao.EM_ABERTO.getDescricao()))
                .withUsuario(buildSelecionavelDto("9"))
                .build();
        DomainException ex = assertThrows(DomainException.class, () -> contaService.cadastrarConta(loginAdmin(), contaRequest));

        assertEquals(MensagemErro.Conta.CONTA_VALOR_NEGATIVO, ex.getMessage());
    }


}
