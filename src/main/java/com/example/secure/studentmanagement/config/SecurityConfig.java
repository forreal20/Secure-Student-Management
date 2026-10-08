package com.example.secure.studentmanagement.config;

import com.example.secure.studentmanagement.security.JwtAthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAthenticationFilter jwtAthenticationFilter;

    public SecurityConfig(JwtAthenticationFilter jwtAthenticationFilter) {
        this.jwtAthenticationFilter = jwtAthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
