package com.speccy.speccy.interfaces.rest;

import com.speccy.speccy.application.model.auth.request.AuthRequest;
import com.speccy.speccy.application.model.auth.request.UserRequest;
import com.speccy.speccy.application.model.auth.response.AuthResponse;
import com.speccy.speccy.application.model.auth.response.UserResponse;
import com.speccy.speccy.application.service.identity.AuthReadService;
import com.speccy.speccy.application.service.identity.AuthWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthWriteService authWriteService;
	private final AuthReadService authReadService;

	@PostMapping("/register")
	public UserResponse register(@Valid @RequestBody UserRequest request) {
		return authWriteService.createOrUpdate(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody AuthRequest request) {
		return authWriteService.login(request);
	}

	@GetMapping("/me")
	public UserResponse me() {
		return authReadService.me();
	}
}
