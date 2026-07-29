package com.company.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record LoginResponse(
            String accessToken,
            String refreshToken,
            String role
    ) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record PasswordResetRequest(@NotBlank @Email String email) {
    }

    public record PasswordResetConfirm(
            @NotBlank String token,
            @NotBlank String newPassword
    ) {
    }
}
