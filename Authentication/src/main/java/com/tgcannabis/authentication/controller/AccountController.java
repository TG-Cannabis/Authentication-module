package com.tgcannabis.authentication.controller;

import com.tgcannabis.authentication.model.Account;
import com.tgcannabis.authentication.security.JWTTokenService;
import com.tgcannabis.authentication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private JWTTokenService jwtTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para registrar una cuenta
    @PostMapping("/register")
    public ResponseEntity<Object> registerAccount(@RequestBody Account account) {
        Account savedAccount;
        try
        {
            savedAccount = accountService.saveAccount(account);
        }catch(IllegalStateException ex)
        {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    // Endpoint para login (se puede hacer con validación de credenciales)
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
        System.out.println("Contraseña ingresada: " + account.getPassword());
        System.out.println("Contraseña almacenada: " + existingAccount.get().getPassword());
        System.out.println("Match: " + passwordEncoder.matches(account.getPassword(), existingAccount.get().getPassword()));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    // Endpoint para actualizar la información de la cuenta
    @PutMapping("/update-info")
    public ResponseEntity<Account> updateAccountInfo(@RequestBody Account updatedAccount) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(updatedAccount.getEmail());
        if (existingAccount.isPresent()) {
            Account savedAccount = accountService.updateAccount(updatedAccount);
            return ResponseEntity.ok(savedAccount);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Endpoint para eliminar una cuenta
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteAccount(@PathVariable String email) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(email);
        if (existingAccount.isPresent()) {
            accountService.deleteAccountByEmail(email);
            return ResponseEntity.ok("Cuenta eliminada");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
    }
}
