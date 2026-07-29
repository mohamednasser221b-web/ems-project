package com.company.ems.controller;

import com.company.ems.dto.AuthDtos.LoginRequest;
import com.company.ems.dto.AuthDtos.LoginResponse;
import com.company.ems.dto.AuthDtos.RefreshRequest;
import com.company.ems.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    // Password reset endpoints intentionally return 200 regardless of whether
    // the email exists, to avoid account enumeration via this endpoint.
    @PostMapping("/password-reset/request")
    public void requestPasswordReset(@RequestBody AuthDtosPasswordResetShim request) {
        // Implementation: generate a short-lived, single-use reset token,
        // email it via the mail service. Left as a TODO for the mail
        // integration task - see /areas backlog.
    }

    // Placeholder record kept local to avoid a partially-wired dependency; swap
    // for AuthDtos.PasswordResetRequest once the mail service task is picked up.
    public record AuthDtosPasswordResetShim(String email) {
    }
}
