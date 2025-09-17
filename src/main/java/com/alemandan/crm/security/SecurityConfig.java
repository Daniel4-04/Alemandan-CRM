package com.alemandan.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // Delegating: {bcrypt} por defecto; soporta {noop}, {pbkdf2}, etc.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}