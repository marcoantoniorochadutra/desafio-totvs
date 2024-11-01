package com.totvs.conta.infra.reader;

import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Slf4j
@Component
public class ContaCsvReader extends CsvReader<ContaDto> {

    @Override
    public ContaDto map(CSVRecord csvRecord) {
        try {
            if (isLinhaConsistente(csvRecord)) {

                LocalDate dataVencimento = buscarDataValida("Data Vencimento", csvRecord);
                LocalDate dataPagamento = buscarDataValida("Data Pagamento", csvRecord);
                Double valor = buscarDoubleValido("Valor", csvRecord);
                String descricao = csvRecord.get("Descrição");
                SelecionavelDto situacao = SelecionavelDto.builder()
                        .withChave(csvRecord.get("Situação"))
                        .build();

                SelecionavelDto tipoConta = SelecionavelDto.builder()
                        .withChave(csvRecord.get("Tipo Conta"))
                        .build();

                return ContaDto.builder()
                        .withDataVencimento(dataVencimento)
                        .withDataPagamento(dataPagamento)
                        .withValor(valor)
                        .withDescricao(descricao)
                        .withSituacao(situacao)
                        .withTipoConta(tipoConta)
                        .build();
            } else {
                log.info("CSV READER | Linha inconsistente não foi processada: {}", csvRecord);
            }

        } catch (Exception e) {
            log.error("CSV READER | Erro ao ler conta: {}", e.getMessage());
        }
        return null;
    }

    private Boolean isLinhaConsistente(CSVRecord csvRecord) {
        return !Arrays.stream(csvRecord.values()).allMatch(s -> s == null || s.trim().isEmpty());
    }
}
