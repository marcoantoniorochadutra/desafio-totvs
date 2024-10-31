package com.totvs.conta.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class DomainException extends RuntimeException {

    private String message;
}
