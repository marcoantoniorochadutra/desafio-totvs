package com.totvs.conta.domain;

import com.totvs.conta.domain.model.usuario.NivelUsuario;
import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.domain.exception.DomainException;
import com.totvs.conta.domain.model.conta.Conta;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.domain.model.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContaDomainTest {

    @Test
    public void deveAtualizarParaPago() {
        LocalDate dataBase = LocalDate.now();

        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();

        conta.atualizarSituacao(Situacao.PAGO);

        assertEquals(dataBase, conta.getDataPagamento());
        assertEquals(Situacao.PAGO, conta.getSituacao());
    }

    @Test
    public void naoDeveAtualizarNemDarErro() {

        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();

        conta.atualizarSituacao(Situacao.EM_ABERTO);

        assertEquals(Situacao.EM_ABERTO, conta.getSituacao());
    }

    @Test
    public void deveAtualizarParaCancelado() {
        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();

        conta.atualizarSituacao(Situacao.CANCELADO);

        assertNull(conta.getDataPagamento());
        assertEquals(Situacao.CANCELADO, conta.getSituacao());
    }

    @Test
    public void deveAtualizarParaAtrasado() {
        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().minusDays(1))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();

        conta.atualizarSituacao(Situacao.ATRASADO);

        assertNull(conta.getDataPagamento());
        assertEquals(Situacao.ATRASADO, conta.getSituacao());
    }

    @Test
    public void naoDeveAtualizarParaAtrasadoEmDia() {
        LocalDate vencimento = LocalDate.now().plusDays(7);
        Conta conta = Conta.builder()
                .withDataVencimento(vencimento)
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();

        DomainException ex = assertThrows(DomainException.class, () -> conta.atualizarSituacao(Situacao.ATRASADO));

        String expected = String.format(MensagemErro.Conta.CONTA_NAO_ATRASADA, vencimento);
        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void naoDeveAtualizarTransicaoInvalida() {
        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build();
        conta.setSituacao(Situacao.CANCELADO);

        DomainException ex = assertThrows(DomainException.class, () -> conta.atualizarSituacao(Situacao.PAGO));

        assertEquals(MensagemErro.Conta.TRANISCAO_INVALIDA, ex.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoAbertoComPagamento() {
        DomainException ex = assertThrows(DomainException.class, () -> Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withDataPagamento(LocalDate.now())
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(new Usuario())
                .build());

        assertEquals(MensagemErro.Conta.CONTA_ABERTA_COM_PAGAMENTO, ex.getMessage());
    }

    @Test
    public void deveLancarExcecaoPagaSemDataPagamento() {
        DomainException ex = assertThrows(DomainException.class, () -> Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.PAGO)
                .withUsuario(new Usuario())
                .build());

        assertEquals(MensagemErro.Conta.CONTA_NAO_PAGA, ex.getMessage());
    }


    @Test
    public void deveLancarExcecaoUsuarioSemPermissao() {
        Usuario usuario = Usuario.builder().withId(19L).withNivelUsuario(NivelUsuario.USUARIO).build();
        Usuario usuarioSemAuth = Usuario.builder().withId(29L).withNivelUsuario(NivelUsuario.USUARIO).build();

        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(7))
                .withValor(1000.0)
                .withDescricao("Troca de óleo")
                .withSituacao(Situacao.EM_ABERTO)
                .withUsuario(usuario)
                .build();

        DomainException ex = assertThrows(DomainException.class, () -> conta.validarPermissaoUsuario(usuarioSemAuth));

        assertEquals(MensagemErro.Genericos.USUARIO_NAO_AUTORIZADO, ex.getMessage());
    }

}
