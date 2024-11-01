package com.totvs.conta.infra.security;

import com.totvs.conta.application.exception.AuthError;
import com.totvs.conta.application.exception.AuthException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SenhaUtils {

    private static final short MIN_LENGTH = 6;
    private static final short MAX_LENGTH = 20;
    public static final String PASSWORD_PATTERN = String.format("(^(?=.*[a-zA-Z])(?=.*\\d)[\\S]{%d,%d}$)", MIN_LENGTH, MAX_LENGTH);

    public static String encodeSenha(String input) {
        if (input != null) {
            try {
                String sen = "";
                MessageDigest md = MessageDigest.getInstance("MD5");
                BigInteger hash = new BigInteger(1, md.digest(input.getBytes()));
                sen = hash.toString(16);
                return sen;
            } catch (Exception ex) {
                log.error("Erro ao gerar hash", ex);
            }
        } else {
            return null;
        }
        return null;
    }

    public static void validarSenha(String password) {
        if (!senhaValida(password)) {
            AuthError authError = AuthError.SENHA_FRACA;
            String format = String.format(authError.getDescricao(), MIN_LENGTH, MAX_LENGTH);
            throw new AuthException(authError, format);
        }
    }

    private static boolean senhaValida(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
