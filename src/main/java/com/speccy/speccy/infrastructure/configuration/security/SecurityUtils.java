package com.speccy.speccy.infrastructure.configuration.security;

import com.speccy.speccy.application.exception.ConstraintViolationException;
import com.speccy.speccy.application.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }

        if (!(authentication instanceof CustomUserAuthentication customUserAuthentication)) {
            throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }

        Long currentUserId = customUserAuthentication.getUserId();
        if (Objects.isNull(currentUserId)) {
            throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }

        return currentUserId;
    }
}
