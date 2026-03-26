package com.speccy.speccy.infrastructure.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${cors.allowed-origins}")
    private String corsAllowedOrigins;

    @Value("${cors.enabled}")
    private boolean corsEnabled;

    private static final String[] DOC_PUBLIC_URLS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**"
    };

    private static final String[] QUERY_PUBLIC_URLS = {

    };

    private static final String[] COMMAND_PUBLIC_URLS = {

    };

    private static final String[] SYNC_URLS = {"/api/sync/**"};

//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling(
//                        exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .requestMatchers(DOC_PUBLIC_URLS)
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, QUERY_PUBLIC_URLS)
                                        .permitAll()
                                        .requestMatchers(COMMAND_PUBLIC_URLS)
                                        .permitAll()
                                        .requestMatchers(SYNC_URLS)
                                        .permitAll()
                                        .anyRequest()
                                        .permitAll())
                .build();
    }

    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins =
                corsEnabled
                        ? Arrays.stream(corsAllowedOrigins.split(",")).toList()
                        : Collections.singletonList(CorsConfiguration.ALL);

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of(HttpHeaders.CONTENT_DISPOSITION));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
