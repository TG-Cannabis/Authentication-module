package com.tgcannabis.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.security.JWTTokenService;
import com.tgcannabis.authentication.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JWTTokenService jwtTokenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerAccount_shouldReturnCreatedAccount() throws Exception {
        Account account = new Account("test@example.com", "1234");
        when(accountService.saveAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_shouldReturnTokenIfCredentialsMatch() throws Exception {
        Account account = new Account("test@example.com", "rawPassword");
        Account saved = new Account("test@example.com", "encodedPassword");

        when(accountService.getAccountByEmail("test@example.com")).thenReturn(Optional.of(saved));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtTokenService.generateToken("test@example.com")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void registerAccount_shouldReturnConflictIfEmailExists() throws Exception {
        Account account = new Account("test@example.com", "1234");

        when(accountService.saveAccount(any(Account.class)))
                .thenThrow(new IllegalStateException("Email already registered"));

        mockMvc.perform(post("/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already registered"));
    }

    @Test
    void login_shouldReturnUnauthorizedIfCredentialsAreWrong() throws Exception {
        Account account = new Account("test@example.com", "wrongPassword");
        Account saved = new Account("test@example.com", "encodedPassword");

        when(accountService.getAccountByEmail("test@example.com")).thenReturn(Optional.of(saved));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Wrong credentials"));
    }

    @Test
    void updateAccount_shouldUpdateIfExists() throws Exception {
        Account updated = new Account("test@example.com", "updatedPass");

        when(accountService.getAccountByEmail("test@example.com")).thenReturn(Optional.of(updated));
        when(accountService.updateAccount(any(Account.class))).thenReturn(updated);

        mockMvc.perform(put("/accounts/update-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateAccount_shouldReturnNotFoundIfNotExists() throws Exception {
        Account updated = new Account("notfound@example.com", "updatedPass");

        when(accountService.getAccountByEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/accounts/update-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAccount_shouldDeleteIfExists() throws Exception {
        String email = "delete@example.com";
        Account account = new Account(email, "pass");

        when(accountService.getAccountByEmail(email)).thenReturn(Optional.of(account));

        mockMvc.perform(delete("/accounts/delete/{email}", email))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccount_shouldReturnOkEvenIfNotFound() throws Exception {
        String email = "notfound@example.com";

        when(accountService.getAccountByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/accounts/delete/{email}", email))
                .andExpect(status().isOk());
    }
}
