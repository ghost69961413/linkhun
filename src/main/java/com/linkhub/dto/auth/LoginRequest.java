package com.linkhub.dto.auth;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    @JsonAlias("email")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

}
