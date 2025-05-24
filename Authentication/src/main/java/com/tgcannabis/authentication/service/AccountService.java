package com.tgcannabis.authentication.service;

import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) throws IllegalStateException {
        boolean existingAccount = accountRepository.existsByEmail(account.getEmail());
        if (existingAccount) {
            throw new IllegalStateException("Email already registered");
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) throws IllegalStateException {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findById(email);
    }

    public void deleteAccountByEmail(String email) {
        accountRepository.deleteById(email);
    }
}
