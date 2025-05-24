package com.tgcannabis.authentication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {
    @InjectMocks
    private JWTAuthenticationFilter filter;

    @Mock
    private JWTTokenService jwtTokenService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateAndSetSecurityContext_whenTokenIsValid() throws ServletException, IOException {
        // Arrange
        String token = "valid.token.here";
        String email = "user@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenService.validateToken(token)).thenReturn(true);
        when(jwtTokenService.getUserId(token)).thenReturn(email);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());

        verify(jwtTokenService).validateToken(token);
        verify(jwtTokenService).getUserId(token);
        verify(customUserDetailsService).loadUserByUsername(email);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthentication_whenTokenIsMissing() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtTokenService, customUserDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthentication_whenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        String token = "invalid.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenService.validateToken(token)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenService).validateToken(token);
        verifyNoMoreInteractions(jwtTokenService, customUserDetailsService);
        verify(filterChain).doFilter(request, response);
    }
}
