package com.speccy.speccy.application.service.identity.impl;

import com.speccy.speccy.application.exception.ConstraintViolationException;
import com.speccy.speccy.application.exception.ErrorCode;
import com.speccy.speccy.application.mapper.AuthMapper;
import com.speccy.speccy.application.model.auth.request.AuthRequest;
import com.speccy.speccy.application.model.auth.request.UserRequest;
import com.speccy.speccy.application.model.auth.response.AuthResponse;
import com.speccy.speccy.application.model.auth.response.UserResponse;
import com.speccy.speccy.domain.identity.model.ProjectRole;
import com.speccy.speccy.domain.identity.model.Role;
import com.speccy.speccy.domain.identity.model.User;
import com.speccy.speccy.domain.identity.model.UserStatus;
import com.speccy.speccy.domain.identity.persistence.RoleRepository;
import com.speccy.speccy.domain.identity.persistence.UserRepository;
import com.speccy.speccy.infrastructure.configuration.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthWriteService implements com.speccy.speccy.application.service.identity.AuthWriteService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final AuthMapper authMapper;

	@Override
    @Transactional
	public UserResponse createOrUpdate(UserRequest request) {
		String username = request.getUsername().strip();
		String email = request.getEmail().strip().toLowerCase();
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = userRepository.findByUsername(username).orElse(null);
		User userByEmail = userRepository.findByEmail(email).orElse(null);

		if (userByEmail != null && (user == null || !Objects.equals(userByEmail.getId(), user.getId()))) {
			throw new ConstraintViolationException(
					ErrorCode.INVALID_REQUEST,
					"Email is already in use",
					List.of("email"));
		}

		if (user == null) {
			user = new User(username, email, encodedPassword, username);
		} else {
			user.changeEmail(email);
			user.changePassword(encodedPassword);
			if (!Objects.equals(user.getStatus(), UserStatus.ACTIVE)) {
				user.activate();
			}
		}

		user.addRole(getOrCreateDefaultRole());

		userRepository.save(user);
		return authMapper.toUserResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResponse login(AuthRequest request) {
		String username = request.getUsername().strip();

		User user = userRepository.findByUsername(username)
						        .orElseThrow(
								        () -> new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Invalid username or password"));

		if (!Objects.equals(user.getStatus(), UserStatus.ACTIVE)) {
			throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "User is not active");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Invalid username or password");
		}

		List<String> roles = user.getRoles().stream().map(Role::getCode).toList();
		if (roles.isEmpty()) {
			roles = List.of(ProjectRole.DEVELOPER.name());
		}

		String accessToken = tokenProvider.buildAccessToken(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
							roles);

		String refreshToken = tokenProvider.buildRefreshToken(
						    user.getId(),
						    user.getUsername(),
						    user.getEmail());

		return authMapper.toAuthResponse(user, accessToken, refreshToken);
	}

	private Role getOrCreateDefaultRole() {
		return roleRepository.findByCode(ProjectRole.DEVELOPER.name())
                        .orElseGet(
                                () -> {
                                    Role role = new Role(ProjectRole.DEVELOPER.name(), "Default developer role");
                                    roleRepository.save(role);
                                    return role;
						});
	}
}
