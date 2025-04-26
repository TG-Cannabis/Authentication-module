package com.authentication.repository;

import com.authentication.model.Account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Corrected custom query to check if the email exists
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.email = :email")
    boolean existsByEmail(@Param("email") String email);

    // You can also define a simple method without @Query
    Optional<Account> findByEmail(String email);
}


