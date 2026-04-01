package com.speccy.speccy.application.mapper;

import com.speccy.speccy.application.model.auth.response.AuthResponse;
import com.speccy.speccy.application.model.auth.response.UserResponse;
import com.speccy.speccy.domain.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "status", expression = "java(user.getStatus() != null ? user.getStatus().name() : null)")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    AuthResponse toAuthResponse(User user, String accessToken, String refreshToken);
}
