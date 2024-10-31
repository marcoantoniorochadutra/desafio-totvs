package com.totvs.conta.infra.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SenhaEncoder {

    public static String encodeSenha(String input){
        if (input != null){
            String sen = "";
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            BigInteger hash = new BigInteger(1, md.digest(input.getBytes()));
            sen = hash.toString(16);
            return sen;
        }else {
            return null;
        }
    }
}
