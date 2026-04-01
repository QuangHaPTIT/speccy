package com.speccy.speccy.infrastructure.configuration.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

import static com.speccy.speccy.application.constants.GlobalConstants.PREFIX_ROLE;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission) {

        if (!(permission instanceof String requiredPermission)) {
            return false;
        }

        if (!(authentication instanceof CustomUserAuthentication userAuthentication)) {
            return false;
        }

        if (requiredPermission.startsWith(PREFIX_ROLE)) {
            return userAuthentication.hasRole(requiredPermission.substring(5));
        }

        return userAuthentication.hasRole(requiredPermission);
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission) {
        return false;
    }
}
