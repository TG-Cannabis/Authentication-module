package com.tgcannabis.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgcannabis.authentication.config.SecurityConfig;
import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.security.CustomUserDetailsService;
import com.tgcannabis.authentication.security.JWTAuthEntryPoint;
import com.tgcannabis.authentication.security.JWTAuthenticationFilter;
import com.tgcannabis.authentication.security.JWTTokenService;
import com.tgcannabis.authentication.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
