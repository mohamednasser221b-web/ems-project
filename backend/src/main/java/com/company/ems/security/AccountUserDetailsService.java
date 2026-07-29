package com.company.ems.security;

import com.company.ems.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return accountRepository.findByEmail(email)
                .map(AccountPrincipal::new)
                // Same generic message whether the email exists or not -
                // don't let this endpoint become a way to enumerate valid accounts.
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }
}
