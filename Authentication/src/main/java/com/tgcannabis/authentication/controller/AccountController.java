package com.tgcannabis.authentication.controller;

import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.security.JWTTokenService;
import com.tgcannabis.authentication.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JWTTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Object> registerAccount(@RequestBody Account account) {
        Account savedAccount;
        try {
            savedAccount = accountService.saveAccount(account);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(account.getEmail());
        if (existingAccount.isPresent() &&
                passwordEncoder.matches(account.getPassword(), existingAccount.get().getPassword())) {
            String token = jwtTokenService.generateToken(account.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
    }

    @PutMapping("/update-info")
    public ResponseEntity<Account> updateAccountInfo(@RequestBody Account updatedAccount) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(updatedAccount.getEmail());
        if (existingAccount.isPresent()) {
            Account savedAccount = accountService.updateAccount(updatedAccount);
            return ResponseEntity.ok(savedAccount);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String email) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(email);
        if (existingAccount.isPresent()) {
            accountService.deleteAccountByEmail(email);
        }
        return ResponseEntity.ok().build();
    }
}
