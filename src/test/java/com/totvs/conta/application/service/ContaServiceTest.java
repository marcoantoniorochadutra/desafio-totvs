package com.totvs.conta.application.service;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.domain.exception.DomainException;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.infra.support.TestSupport;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.PaginacaoResultDto;
import com.totvs.conta.interfaces.conta.dto.ContaPagamentoDto;
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
                .withSituacao(buildSelecionavelDto(Situacao.EM_ABERTO.name(), Situacao.EM_ABERTO.getDescricao()))
                .withUsuario(buildSelecionavelDto("9"))
                .build();

        ContaDto contaAtualizada = contaService.atualizarConta(loginAdmin(), 15L, contaRequest);

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

        ContaDto contaAtualizada = contaService.alterarSituacaoConta(login(), 16L, Situacao.PAGO);

        assertEquals(16L, contaAtualizada.id());
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
        filtros.put("dataVencimento", LocalDate.now().plusDays(30));
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
    public void deveBuscarTotalPagoNoPeriodo() {

        LocalDate dataBase = LocalDate.now().plusDays(50);

        criarConta(usuarioAdmin(), Situacao.PAGO, 4500.0, "Pagamento João", dataBase);
        criarConta(usuarioAdmin(), Situacao.PAGO, 2500.0, "Pagamento Cleber", dataBase);
        criarConta(usuarioAdmin(), Situacao.ATRASADO, 1500.0, "Pagamento Batata");
        criarConta(usuarioAdmin(), Situacao.EM_ABERTO, 1500.0, "Pagamento Batata");
        criarConta(usuarioAdmin(), Situacao.CANCELADO, 1500.0, "Pagamento Batata", dataBase);
        criarConta(usuarioAdmin(), Situacao.PAGO, 100.0, "Pagamento Gustavo", dataBase);


        Map<String, Object> filtros = new HashMap<>();
        filtros.put("usuarioId", 9L);
        filtros.put("dataInicial", dataBase.minusDays(1));
        filtros.put("dataFinal", dataBase.plusDays(2));


        ContaPagamentoDto contaPagamento = contaService.obterValorTotalPagoPorPeriodo(loginAdmin(), filtros);

        assertEquals(7100.0, contaPagamento.valorTotal());

    }

    @Test
    public void deveBuscarContaPorIdUsuarioAdmin() {

        ContaDto contaPagamento = contaService.obterContaPorId(loginAdmin(), 19L);

        assertEquals(19L, contaPagamento.id());
        assertEquals(550.0, contaPagamento.valor());
        assertEquals("Pagamento de Mercado", contaPagamento.descricao());

    }

    @Test
    public void naoDeveBuscarContaDiferentesUsuarios() {
        DomainException ex = assertThrows(DomainException.class, () -> contaService.obterContaPorId(login(), 17L));
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


}
