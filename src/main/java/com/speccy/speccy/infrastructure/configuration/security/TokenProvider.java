package com.speccy.speccy.infrastructure.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider {

    private final String secretKey;
    private final String issuer;
    private final Long expirationMinute;

    public TokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expirationMinute}") Long expirationMinute) {
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.expirationMinute = expirationMinute;
    }

    public String buildAccessToken(
            Long userId,
            String username,
            String email,
            List<String> roles) {

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationMinute * 60);

        var jwtBuilder = JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
            .withClaim("username", username)
                .withClaim("email", email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt));

        if (roles != null && !roles.isEmpty()) {
            jwtBuilder.withArrayClaim("roles", roles.toArray(String[]::new));
        }

        return jwtBuilder.sign(algorithm());
    }

    public String buildRefreshToken(Long userId, String username, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationMinute * 60 * 24L * 7);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withClaim("email", email)
                .withClaim("typ", "refresh")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm());
    }

    public boolean isValidToken(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        try {
            return Long.valueOf(verify(token).getSubject());
        } catch (Exception ex) {
            return null;
        }
    }

    public String extractEmail(String token) {
        try {
            return verify(token).getClaim("email").asString();
        } catch (Exception ex) {
            return null;
        }
    }

    public String extractUsername(String token) {
        try {
            return verify(token).getClaim("username").asString();
        } catch (Exception ex) {
            return null;
        }
    }

    public Date extractIssuedAt(String token) {
        try {
            return verify(token).getIssuedAt();
        } catch (Exception ex) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return verify(token).getExpiresAt();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<String> extractRoles(String token) {
        return extractArrayClaim(token, "roles");
    }

    private List<String> extractArrayClaim(String token, String claimName) {
        try {
            Claim claim = verify(token).getClaim(claimName);
            if (claim == null || claim.isNull()) {
                return Collections.emptyList();
            }

            String[] values = claim.asArray(String.class);
            if (values == null || values.length == 0) {
                return Collections.emptyList();
            }

            return List.of(values);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private DecodedJWT verify(String token) {
        return verifier().verify(token);
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    private JWTVerifier verifier() {
        return JWT.require(algorithm())
                .withIssuer(issuer)
                .build();
    }
}
