package com.speccy.speccy.application.service.identity;

import com.speccy.speccy.application.model.auth.response.UserResponse;

public interface AuthReadService {
	UserResponse me();
}
