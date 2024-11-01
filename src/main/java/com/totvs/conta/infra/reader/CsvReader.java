package com.totvs.conta.infra.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;


public abstract class CsvReader<T> {


    public List<T> read(byte[] bytes, String... header) {
        CSVParser csvParser = createReader(bytes, header);
        return csvParser.stream().map(this::map).toList();
    }

    private CSVParser createReader(byte[] bytes, String[] header) {

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Reader reader = new InputStreamReader(byteArrayInputStream);
            return CSVFormat.Builder.create()
                    .setHeader(header)
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected LocalDate buscarDataValida(String data, CSVRecord csvRecord) {
        String input = csvRecord.get(data);
        if (StringUtils.isNotBlank(input)) {
            return LocalDate.parse(input);
        }
        return null;
    }

    protected Double buscarDoubleValido(String data, CSVRecord csvRecord) {
        String input = csvRecord.get(data);
        return NumberUtils.toDouble(input);
    }


    public abstract T map(CSVRecord csvRecord);

}
