package com.totvs.conta.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totvs.conta.shared.dto.LoginContextDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtProvider {


    private static final String LOGIN_CONTEXT = "login";

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("SzdThWLbUbLQDGWogQkDnpxVgITHibjrlrxyoZdk3JitJs7S9Wq975F5e8AWxyDA".getBytes(StandardCharsets.UTF_8));
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder();

    private static final ObjectMapper MAPPER = new ObjectMapper();


    public static String generate(LoginContextDto loginContext) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plus(5, ChronoUnit.MINUTES);
        return Jwts.builder()
                .claim(LOGIN_CONTEXT, loginContext)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(SECRET_KEY)
                .compressWith(Jwts.ZIP.DEF)
                .compact();
    }

    public static LoginContextDto getLoginContext(String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);

        Object loginClaim = claims.getPayload().get(LOGIN_CONTEXT);
        return MAPPER.convertValue(loginClaim, LoginContextDto.class);
    }

    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[24];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_ENCODER.encodeToString(randomBytes);
    }
}
