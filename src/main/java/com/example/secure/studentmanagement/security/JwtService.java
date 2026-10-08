package com.example.secure.studentmanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret:QmFzZTY0U2VjcmV0S2V5Rm9yU2VjdXJlU3R1ZGVudE1hbmFnZW1lbnQ=}") String secret,
            @Value("${app.jwt.expiration-ms:900000}") long expirationMs) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateAccessToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + expirationMs
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {

        Claims claims = extractAllClaims(token);

        return username.equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}