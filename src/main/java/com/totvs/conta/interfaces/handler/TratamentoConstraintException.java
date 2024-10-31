package com.totvs.conta.interfaces.handler;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.shared.dto.MensagemDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TratamentoConstraintException {

    public static MensagemDto tratar(ConstraintViolationException ex) {
        return new MensagemDto(buildMessage(ex.getConstraintViolations()));
    }

    private static String buildMessage(Set<ConstraintViolation<?>> violations) {

        StringBuilder sb = new StringBuilder();

        List<String> nullFields = new ArrayList<>();

        List<String> patternFields = new ArrayList<>();

        List<String> sizeFields = new ArrayList<>();

        List<String> numberFields = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations) {
            if (!matchNullAnnotation(violation, nullFields)
                    && !matchPatternAnnotation(violation, patternFields)
                    && !matchSizeAnnotaion(violation, sizeFields)
                    && !matchNumberAnnotaion(violation, numberFields)) {
                sb.append(String.format(" %s ", violation.getMessageTemplate()));
            }
        }

        if (!nullFields.isEmpty()) {
            sb.append(String.format(" %s  %s.", MensagemErro.Genericos.CAMPO_NECESSARIO, nullFields));
        }

        if (!patternFields.isEmpty()) {
            sb.append(String.format(" %s  %s.", MensagemErro.Genericos.INFORMACAO_INVALIDA, patternFields));
        }

        if (!sizeFields.isEmpty()) {
            sb.append(String.format(" %s  %s.", MensagemErro.Genericos.TAMANHO_INVALIDO, sizeFields));
        }

        if (!numberFields.isEmpty()) {
            sb.append(String.format(" %s  %s.", MensagemErro.Genericos.INFORMACAO_INVALIDA, numberFields));
        }

        return sb.toString();
    }

    private static boolean matchNullAnnotation(ConstraintViolation<?> violation, List<String> nullFields) {
        boolean isNullAnnotation = false;
        if (violation.getConstraintDescriptor().getAnnotation() instanceof NotNull) {
            nullFields.add(violation.getPropertyPath().toString());
            isNullAnnotation = true;
        }
        return isNullAnnotation;
    }

    private static boolean matchPatternAnnotation(ConstraintViolation<?> violation, List<String> patternFields) {
        boolean isPatternAnnotation = false;
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Pattern) {
            patternFields.add(violation.getPropertyPath().toString());
            isPatternAnnotation = true;
        }
        return isPatternAnnotation;
    }

    private static boolean matchSizeAnnotaion(ConstraintViolation<?> violation, List<String> sizeFields) {
        boolean isSizeAnnotation = false;
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Size) {
            sizeFields.add(violation.getPropertyPath().toString());
            isSizeAnnotation = true;
        }
        return isSizeAnnotation;
    }

    private static boolean matchNumberAnnotaion(ConstraintViolation<?> violation, List<String> numberFields) {
        boolean isSizeAnnotation = false;
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Min
                || violation.getConstraintDescriptor().getAnnotation() instanceof Max) {
            numberFields.add(violation.getPropertyPath().toString());
            isSizeAnnotation = true;
        }
        return isSizeAnnotation;
    }

}
