package com.speccy.speccy.infrastructure.configuration.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserAuthentication extends UsernamePasswordAuthenticationToken {

    private final Long userId;
    private final String token;
    private final List<String> roles;

    public CustomUserAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
        this.userId = null;
        this.token = null;
        this.roles = Collections.emptyList();
    }

    public CustomUserAuthentication(
            Object principal,
            Object credentials,
            Long userId,
            String token,
            List<String> roles) {
        super(principal, credentials, buildAuthorities(roles));
        this.userId = userId;
        this.token = token;
        this.roles = roles != null ? List.copyOf(roles) : Collections.emptyList();
    }

    private static Collection<GrantedAuthority> buildAuthorities(List<String> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        return authorities;
    }

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(value -> value.equalsIgnoreCase(role));
    }
}
