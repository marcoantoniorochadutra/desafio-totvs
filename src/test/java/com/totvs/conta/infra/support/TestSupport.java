package com.totvs.conta.infra.support;

import com.totvs.conta.domain.model.conta.TipoConta;
import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import com.totvs.conta.domain.model.conta.Conta;
import com.totvs.conta.domain.model.conta.ContaRepository;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.domain.model.usuario.Usuario;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import com.totvs.conta.infra.configuration.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

@ActiveProfiles("test")
@Sql("classpath:sql/test-data.sql")
@ContextConfiguration(classes = {AppConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestSupport extends TestContainerSupport{

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    protected LoginContextDto loginAdmin() {
        SelecionavelDto tipo = buildSelecionavelDto("ADMIN");
        return new LoginContextDto(9L, "admin@email.com", tipo);
    }

    protected LoginContextDto login() {
        SelecionavelDto tipo = buildSelecionavelDto("USUARIO");
        return new LoginContextDto(10L, "mock_sql@email.com", tipo);
    }

    protected LoginContextDto loginInvalid() {
        SelecionavelDto tipo = buildSelecionavelDto("USUARIO");
        return new LoginContextDto(189749L, "invalid@email.com", tipo);
    }

    protected SelecionavelDto buildSelecionavelDto(String chave) {
        return buildSelecionavelDto(chave, null);
    }

    protected SelecionavelDto buildSelecionavelDto(String chave, String valor) {
        return SelecionavelDto.builder().withChave(chave).withValor(valor).build();
    }

    protected Usuario usuarioAdmin() {
        return usuarioRepository.buscarPorId(9L);
    }

    protected Usuario usuario() {
        return usuarioRepository.buscarPorId(10L);
    }

    protected Conta criarConta(Usuario usuario, Situacao situacao, Double valor, String descricao) {
        return criarConta(usuario, situacao, valor, descricao, null);
    }

    protected Conta criarConta(Usuario usuario, Situacao situacao, Double valor, String descricao, LocalDate dataPagamento) {
        Conta conta = Conta.builder()
                .withDataVencimento(LocalDate.now().plusDays(10))
                .withTipoConta(TipoConta.MERCADO)
                .withDataPagamento(dataPagamento)
                .withValor(valor)
                .withDescricao(descricao)
                .withSituacao(situacao)
                .withUsuario(usuario)
                .build();

        return contaRepository.salvar(conta);
    }

}
