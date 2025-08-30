package com.agrocrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.accessTokenExpirationMinutes:30}")
    private long accessTokenExpirationMinutes;

    @Value("${app.jwt.refreshTokenExpirationDays:30}")
    private long refreshTokenExpirationDays;

    private Key getSigningKey() {
        // Убеждаемся, что ключ достаточно длинный для HS512 (минимум 512 бит = 64 байта)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            // Если ключ слишком короткий, дополняем его
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 64));
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, String role, String sessionId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenExpirationMinutes * 60);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role, "sessionId", sessionId, "type", "access"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateAccessToken(String username, java.util.List<String> roles, String sessionId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenExpirationMinutes * 60);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("roles", roles, "sessionId", sessionId, "type", "access"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username, String sessionId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("sessionId", sessionId, "type", "refresh"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parse(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = parse(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
}
