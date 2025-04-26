package com.authentication.controller;

import com.authentication.model.Account;
import com.authentication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

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
    public ResponseEntity<String> login(@RequestBody Account account) {
        Optional<Account> existingAccount = accountService.getAccountByEmail(account.getEmail());
        if (existingAccount.isPresent() && existingAccount.get().getPassword().equals(account.getPassword())) {
            return ResponseEntity.ok("Login exitoso");
        }
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
