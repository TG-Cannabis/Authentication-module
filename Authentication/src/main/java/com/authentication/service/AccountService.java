package com.authentication.service;

import com.authentication.model.Account;
import com.authentication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private  AccountRepository accountRepository;

    // Crear una cuenta
    public Account saveAccount(Account account) throws IllegalStateException {
        boolean existingAccount = accountRepository.existsByEmail(account.getEmail());
        if (existingAccount) {
            throw new IllegalStateException("El correo ya está registrado");
        }
        return accountRepository.save(account);
    }
    // Actualizar cuenta:
    public Account updateAccount(Account account) throws IllegalStateException {
        return accountRepository.save(account);
    }

    // Obtener una cuenta por email
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findById(email);
    }

    // Obtener todas las cuentas
    public Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Eliminar una cuenta por email
    public void deleteAccountByEmail(String email) {
        accountRepository.deleteById(email);
    }
}
