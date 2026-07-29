package com.company.ems.service;

import com.company.ems.dto.AuthDtos.LoginRequest;
import com.company.ems.dto.AuthDtos.LoginResponse;
import com.company.ems.entity.Account;
import com.company.ems.exception.ApiExceptions.BadRequestException;
import com.company.ems.repository.AccountRepository;
import com.company.ems.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        Account account = accountRepository.findByEmail(request.email())
                // Same message as a wrong password - don't reveal whether the
                // email exists (account enumeration).
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!account.isActive()) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            auditService.record(account.getId(), "Account", account.getId(),
                    "login_attempt", null, "FAILED", ipAddress);
            throw new BadRequestException("Invalid email or password");
        }

        account.setLastLoginAt(Instant.now());
        accountRepository.save(account);

        auditService.record(account.getId(), "Account", account.getId(),
                "login_attempt", null, "SUCCESS", ipAddress);

        String accessToken = jwtService.generateAccessToken(account.getId(), account.getEmail(), account.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(account.getId());

        return new LoginResponse(accessToken, refreshToken, account.getRole().name());
    }

    public LoginResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)
                || !"refresh".equals(jwtService.extractClaim(refreshToken, "type"))) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        var accountId = java.util.UUID.fromString(jwtService.extractSubject(refreshToken));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

        if (!account.isActive()) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(account.getId(), account.getEmail(), account.getRole().name());
        // Refresh token rotation would go here in a hardened version:
        // issue a new refresh token too and invalidate the old one server-side
        // (requires a token store - see the "still to harden" note in the README).
        return new LoginResponse(newAccessToken, refreshToken, account.getRole().name());
    }
}
