package com.speccy.speccy.application.model.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private Long id;
	private String username;
	private String email;
	private String accessToken;
	private String refreshToken;
}
