package com.example.mybank.infrastructure.driving.rest.security;

import com.example.mybank.domain.model.Client;
import com.example.mybank.infrastructure.driving.rest.security.JwtAuthenticationFilter.MyBankPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class JwtTokenService {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        if (secret.length() < 32) {
            secret = String.format("%-32s", secret).replace(' ', 'x');
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    public Map<String, Object> parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public static Client.Id extractClientId() {
        var auth = getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AuthenticationException("Invalid token") {
            };
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof MyBankPrincipal p) {
            return p.clientId();
        }
        throw new AuthenticationException("Invalid token") {
        };
    }
}
