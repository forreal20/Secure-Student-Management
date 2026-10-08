package com.example.secure.studentmanagement.auth;

import com.example.secure.studentmanagement.user.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setExpiryDate(
                Instant.now().plus(
                        refreshExpirationMs,
                        ChronoUnit.MILLIS
                )
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Invalid refresh token"
                                ));

        if (refreshToken.isRevoked()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(Instant.now())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Invalid refresh token"
                                ));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}