package com.linkhub.dto.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {
    @NotBlank(message = "New email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email must be 100 characters or fewer")
    private String newEmail;

    @NotBlank(message = "Current password is required")
    private String password;
}
