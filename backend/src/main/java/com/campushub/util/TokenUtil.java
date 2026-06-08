package com.campushub.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public final class TokenUtil {

    private static final SecretKey SECRET = Keys.hmacShaKeyFor(
            "CampusHub-SecretKey-2026-Must-Be-At-Least-256-Bits!!".getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    private TokenUtil() {
    }

    public static String generateToken(UUID userUuid) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userUuid.toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRATION_MS))
                .signWith(SECRET)
                .compact();
    }

    public static UUID parseToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String normalizedToken = token;
        if (normalizedToken.startsWith("Bearer ")) {
            normalizedToken = normalizedToken.substring("Bearer ".length());
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET)
                    .build()
                    .parseSignedClaims(normalizedToken)
                    .getPayload();
            return UUID.fromString(claims.getSubject());
        } catch (ExpiredJwtException e) {
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isValid(String token) {
        return parseToken(token) != null;
    }
}
