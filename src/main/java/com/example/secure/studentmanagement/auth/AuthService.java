package com.example.secure.studentmanagement.auth;

import com.example.secure.studentmanagement.security.JwtService;
import com.example.secure.studentmanagement.user.Role;
import com.example.secure.studentmanagement.user.User;
import com.example.secure.studentmanagement.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // REGISTER
    @Transactional
    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Never save a plain-text password.
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Public registration always creates a STUDENT.
        user.setRole(Role.STUDENT);

        userRepository.save(user);

        return "Registration successful";
    }

    // LOGIN
    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(
                        user.getUsername()
                );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    // REFRESH ACCESS TOKEN
    @Transactional
    public LoginResponse refreshAccessToken(
            String token) {

        RefreshToken oldToken =
                refreshTokenService.verifyRefreshToken(token);

        User user = oldToken.getUser();

        // Revoke the old refresh token.
        refreshTokenService.revokeRefreshToken(token);

        // Issue a new refresh token.
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        // Issue a new access token.
        String newAccessToken =
                jwtService.generateAccessToken(
                        user.getUsername()
                );

        return new LoginResponse(
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    // LOGOUT
    @Transactional
    public void logout(String token) {

        refreshTokenService.revokeRefreshToken(token);
    }
}