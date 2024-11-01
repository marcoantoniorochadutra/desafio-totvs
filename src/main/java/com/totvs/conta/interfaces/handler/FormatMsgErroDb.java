package com.totvs.conta.interfaces.handler;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.shared.dto.MensagemDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

@Slf4j
public class FormatMsgErroDb {

    public static final int DB_DUPLICATE_KEY = 23505;
    public static final int DB_FIELD_NO_NULL = 23502;

    public static ResponseEntity<Object> tratarExcecao(SQLException ex) {
        return buildMessage(ex);
    }

    private static ResponseEntity<Object> buildMessage(SQLException ex) {
        return switch (NumberUtils.toInt(ex.getSQLState())) {
            case DB_DUPLICATE_KEY -> buildResponseEntity(HttpStatus.CONFLICT, MensagemErro.Genericos.REGISTRO_EXISTENTE);
            case DB_FIELD_NO_NULL -> buildResponseEntity(HttpStatus.BAD_REQUEST, extrairCampo(ex.getMessage()));
            default -> {
                log.warn("SQL error code not mapped {} {}", ex.getErrorCode(), ex.getSQLState());
                yield buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, MensagemErro.Genericos.PROBLEMA_SERVIDOR);
            }
        };
    }

    private static String extrairCampo(String message) {
        int primeiraAspaIdx = message.indexOf('"');
        String corte = message.substring(primeiraAspaIdx + 1);
        int segundaAspaIdx = corte.indexOf('"');
        corte = corte.substring(0, segundaAspaIdx);
        return String.format("%s [ %s ]", MensagemErro.Genericos.CAMPO_NECESSARIO, corte);
    }

    private static ResponseEntity<Object> buildResponseEntity(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(new MensagemDto(errorMessage));
    }
}

