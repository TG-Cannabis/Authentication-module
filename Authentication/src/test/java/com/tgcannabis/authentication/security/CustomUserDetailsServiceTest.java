package com.tgcannabis.authentication.security;

import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    private AccountService accountService;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        userDetailsService = new CustomUserDetailsService(accountService);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenAccountExists() {
        // Given
        Account account = new Account("user@example.com", "encodedPassword");

        when(accountService.getAccountByEmail("user@example.com"))
                .thenReturn(Optional.of(account));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        // Then
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(accountService, times(1)).getAccountByEmail("user@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenAccountDoesNotExist() {
        // Given
        when(accountService.getAccountByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@example.com"));

        verify(accountService, times(1)).getAccountByEmail("missing@example.com");
    }
}
