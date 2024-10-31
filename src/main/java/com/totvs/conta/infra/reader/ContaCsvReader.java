package com.totvs.conta.infra.reader;

import com.totvs.conta.domain.model.conta.Situacao;
import com.totvs.conta.interfaces.conta.dto.ContaDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class ContaCsvReader extends CsvReader<ContaDto> {

    @Override
    public ContaDto map(CSVRecord csvRecord) {
        try {

            LocalDate dataVencimento = buscarDataValida("Data Vencimento", csvRecord);
            LocalDate dataPagamento = buscarDataValida("Data Pagamento", csvRecord);
            Double valor = buscarDoubleValido("Valor", csvRecord);
            String descricao = csvRecord.get("Descrição");
            SelecionavelDto situacao = SelecionavelDto.builder()
                    .withChave(csvRecord.get("Situação"))
                    .build();

            return ContaDto.builder()
                    .withDataVencimento(dataVencimento)
                    .withDataPagamento(dataPagamento)
                    .withValor(valor)
                    .withDescricao(descricao)
                    .withSituacao(situacao)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao ler conta: {}", e.getMessage());
        }
        return null;
    }
}
