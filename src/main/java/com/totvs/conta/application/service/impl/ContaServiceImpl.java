package com.totvs.conta.application.service.impl;

import com.totvs.conta.application.service.ContaService;
import com.totvs.conta.infra.reader.CsvReader;
import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import com.totvs.conta.domain.model.conta.Conta;
import com.totvs.conta.domain.model.conta.ContaRepository;
import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.domain.model.usuario.Usuario;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.PaginacaoResultDto;
import com.totvs.conta.interfaces.conta.dto.ContaPagamentoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class ContaServiceImpl implements ContaService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final CsvReader<ContaDto> csvReader;

    @Autowired
    public ContaServiceImpl(UsuarioRepository usuarioRepository,
                            ContaRepository contaRepository,
                            CsvReader<ContaDto> csvReader) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.csvReader = csvReader;
    }

    @Override
    public ContaDto cadastrarConta(LoginContextDto usuarioContext, ContaDto contaDto) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioContext.id());

        Conta conta = converterDtoParaDominio(usuario, contaDto);

        return saveAndMap(conta);
    }

    @Override
    public ContaDto atualizarConta(LoginContextDto usuarioContext, Long id, ContaDto contaDto) {
        Conta conta = buscarConta(usuarioContext, id);

        atualizarDadosConta(conta, contaDto);

        return saveAndMap(conta);
    }

    @Override
    public ContaDto alterarSituacaoConta(LoginContextDto usuarioContext, Long id, Situacao situacao) {
        Conta conta = buscarConta(usuarioContext, id);
        conta.atualizarSituacao(situacao);
        return saveAndMap(conta);
    }

    @Override
    public PaginacaoResultDto<ContaDto> obterContasParaPagar(LoginContextDto usuarioContext, Pageable pageable, Map<String, Object> filtros) {

        Long usuarioId = usuarioContext.id();
        String descricao = (String) filtros.get("descricao");
        LocalDate dataVencimento = (LocalDate) filtros.get("dataVencimento");
        LocalDate dataInicial = LocalDate.now();
        List<Situacao> situacao = List.of(Situacao.EM_ABERTO, Situacao.ATRASADO);

        Page<Conta> contas = contaRepository.obterContasPagar(situacao, descricao, dataInicial, dataVencimento, usuarioId, pageable);
        return converterParaListaParaResultDto(contas);
    }

    @Override
    public ContaDto obterContaPorId(LoginContextDto usuarioContext, Long id) {
        Conta conta = buscarConta(usuarioContext, id);
        return converterDominioParaDto(conta);
    }

    @Override
    public ContaPagamentoDto obterValorTotalPagoPorPeriodo(LoginContextDto usuarioContext, Map<String, Object> filtros) {
        LocalDate dataInicial = (LocalDate) filtros.get("dataInicial");
        LocalDate dataFinal = (LocalDate) filtros.get("dataFinal");
        Long usuarioId = usuarioContext.id();

        Double valorTotal = contaRepository.obterValorTotalPagarPorPeriodo(dataInicial, dataFinal, usuarioId);

        return new ContaPagamentoDto(valorTotal);
    }

    @Override
    public List<ContaDto> importarContasCsv(LoginContextDto usuarioContext, byte[] bytes) {
        List<ContaDto> contas = csvReader.read(bytes, "Data Vencimento", "Data Pagamento", "Valor", "Descrição", "Situação");
        return contas.stream()
                .map(conta -> importarConta(usuarioContext, conta))
                .filter(Objects::nonNull)
                .toList();
    }

    private ContaDto importarConta(LoginContextDto usuarioContext, ContaDto conta) {
        try {
            return cadastrarConta(usuarioContext, conta);
        } catch (Exception e) {
            log.error("Erro ao importar conta {} | {}", conta, e.getMessage(), e);
        }
        return null;
    }

    private Conta buscarConta(LoginContextDto usuarioContext, Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioContext.id());
        Conta conta = contaRepository.buscarPorId(id);
        conta.validarPermissaoUsuario(usuario);
        return conta;
    }

    private void atualizarDadosConta(Conta conta, ContaDto contaDto) {
        Situacao situacao = Situacao.fromStr(contaDto.situacao().chave());
        conta.atualizarSituacao(situacao);
        conta.setDataVencimento(contaDto.dataVencimento());
        conta.setDataPagamento(contaDto.dataPagamento());
        conta.setValor(contaDto.valor());
        conta.setDescricao(contaDto.descricao());
        conta.validarCampos();
    }

    private ContaDto saveAndMap(Conta conta) {
        Conta resultado = contaRepository.salvar(conta);
        return converterDominioParaDto(resultado);
    }

    private PaginacaoResultDto<ContaDto> converterParaListaParaResultDto(Page<Conta> contas) {
        List<ContaDto> contasDto = contas.stream().map(this::converterDominioParaDto).toList();
        return new PaginacaoResultDto<>(contas.getNumber(), contas.getTotalPages(), contas.getTotalElements(), contasDto);
    }

    private Conta converterDtoParaDominio(Usuario usuario, ContaDto contaDto) {
        Situacao situacao = Situacao.fromStr(contaDto.situacao().chave());
        return Conta.builder()
                .withDataVencimento(contaDto.dataVencimento())
                .withDataPagamento(contaDto.dataPagamento())
                .withValor(contaDto.valor())
                .withDescricao(contaDto.descricao())
                .withSituacao(situacao)
                .withUsuario(usuario)
                .build();
    }

    private ContaDto converterDominioParaDto(Conta conta) {
        return ContaDto.builder()
                .withId(conta.getId())
                .withDataVencimento(conta.getDataVencimento())
                .withDataPagamento(conta.getDataPagamento())
                .withValor(conta.getValor())
                .withDescricao(conta.getDescricao())
                .withSituacao(SelecionavelDto.builder()
                        .withChave(conta.getSituacao().name())
                        .withValor(conta.getSituacao().getDescricao())
                        .build())
                .withUsuario(SelecionavelDto.builder()
                        .withChave(conta.getUsuario().getId().toString())
                        .withValor(conta.getUsuario().getNome())
                        .build())
                .build();
    }
}
