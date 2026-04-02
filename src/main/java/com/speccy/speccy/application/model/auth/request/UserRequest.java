package com.speccy.speccy.application.model.auth.request;

import com.speccy.speccy.application.model.auth.request.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    @NotBlank
    private String confirmPassword;
}
