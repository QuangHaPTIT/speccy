package com.speccy.speccy.application.model.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequest {
    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;
}
