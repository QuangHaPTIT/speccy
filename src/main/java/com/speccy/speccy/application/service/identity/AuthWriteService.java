package com.speccy.speccy.application.service.identity;

import com.speccy.speccy.application.model.auth.request.AuthRequest;
import com.speccy.speccy.application.model.auth.request.UserRequest;
import com.speccy.speccy.application.model.auth.response.AuthResponse;
import com.speccy.speccy.application.model.auth.response.UserResponse;

public interface AuthWriteService {
	UserResponse createOrUpdate(UserRequest request);

	AuthResponse login(AuthRequest request);
}
