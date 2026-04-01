package com.speccy.speccy.application.service.user.impl;

import com.speccy.speccy.application.exception.ConstraintViolationException;
import com.speccy.speccy.application.exception.ErrorCode;
import com.speccy.speccy.application.mapper.AuthMapper;
import com.speccy.speccy.application.model.auth.response.UserResponse;
import com.speccy.speccy.domain.identity.persistence.UserRepository;
import com.speccy.speccy.infrastructure.configuration.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthReadService implements com.speccy.speccy.application.service.user.AuthReadService {

	private final UserRepository userRepository;
	private final AuthMapper authMapper;

	@Override
	public UserResponse me() {
		Long userId = SecurityUtils.getCurrentUserId();

		return userRepository
				.findById(userId)
				.map(authMapper::toUserResponse)
				.orElseThrow(
						() -> new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized"));
	}

}
