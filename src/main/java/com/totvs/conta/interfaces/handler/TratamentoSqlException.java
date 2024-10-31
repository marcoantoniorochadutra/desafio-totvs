package com.totvs.conta.interfaces.handler;

import com.totvs.conta.shared.dto.MensagemDto;

import java.sql.SQLException;

public class TratamentoSqlException {

    public static MensagemDto tratar(SQLException ex) {
        System.err.println(ex.getSQLState());
        return null;

    }
}
