package com.tgcannabis.authentication.service;

import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveAccount_shouldEncodePasswordAndSave() {
        Account input = new Account("test@example.com", "rawPassword");
        Account saved = new Account("test@example.com", "encodedPassword");

        when(accountRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = accountService.saveAccount(input);

        assertEquals("encodedPassword", result.getPassword());
        assertEquals("test@example.com", result.getEmail());

        verify(accountRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("rawPassword");
        verify(accountRepository).save(input);
    }

    @Test
    void saveAccount_shouldThrowExceptionIfEmailExists() {
        Account input = new Account("test@example.com", "rawPassword");

        when(accountRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> accountService.saveAccount(input));

        assertEquals("Email already registered", exception.getMessage());

        verify(accountRepository).existsByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_shouldSaveAndReturnAccount() {
        Account account = new Account("test@example.com", "password");

        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.updateAccount(account);

        assertEquals(account, result);
        verify(accountRepository).save(account);
    }

    @Test
    void getAccountByEmail_shouldReturnAccountIfExists() {
        Account account = new Account("test@example.com", "password");

        when(accountRepository.findById("test@example.com")).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
    }

    @Test
    void deleteAccountByEmail_shouldInvokeRepositoryDelete() {
        String email = "test@example.com";

        doNothing().when(accountRepository).deleteById(email);

        accountService.deleteAccountByEmail(email);

        verify(accountRepository).deleteById(email);
    }
}
