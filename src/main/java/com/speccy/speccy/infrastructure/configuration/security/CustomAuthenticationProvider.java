package com.speccy.speccy.infrastructure.configuration.security;

import com.speccy.speccy.domain.identity.model.User;
import com.speccy.speccy.domain.identity.model.UserStatus;
import com.speccy.speccy.domain.identity.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String credential = authentication.getName();
        String password =
                authentication.getCredentials() != null
                        ? authentication.getCredentials().toString()
                        : null;

        User user =
                userRepository
                .findByUsername(credential)
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!Objects.equals(user.getStatus(), UserStatus.ACTIVE)) {
            throw new DisabledException("User is not active");
        }

        if (password == null
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        log.debug("Authentication succeeded for {}", credential);
        return new UsernamePasswordAuthenticationToken(
                credential,
                null,
                Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
