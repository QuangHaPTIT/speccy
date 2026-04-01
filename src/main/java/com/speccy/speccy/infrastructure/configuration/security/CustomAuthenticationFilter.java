package com.speccy.speccy.infrastructure.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.speccy.speccy.application.constants.GlobalConstants.PREFIX_ROLE;
import static com.speccy.speccy.application.constants.GlobalConstants.PREFIX_TOKEN;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (Objects.isNull(token) || !tokenProvider.isValidToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = tokenProvider.extractUserId(token);
        String username = tokenProvider.extractUsername(token);

        if (Objects.isNull(userId) || Objects.isNull(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> roles = tokenProvider.extractRoles(token);

        User principal =
                new User(
                username,
                        "",
                buildRoleAuthorities(roles));

        CustomUserAuthentication authentication =
            new CustomUserAuthentication(principal, token, userId, token, roles);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> buildRoleAuthorities(List<String> roles) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(PREFIX_ROLE + role)));

        return authorities;
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith(PREFIX_TOKEN)) {
            return authorizationHeader.substring(7);
        }

        return null;
    }
}
