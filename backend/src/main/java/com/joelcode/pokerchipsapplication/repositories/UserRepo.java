package com.joelcode.pokerchipsapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.joelcode.pokerchipsapplication.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByID(UUID id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


    // Stores list of users
    List<User> findByCreatedAtAfter(LocalDateTime createdAtAfter);

    List<User> findByUsernameContainingIgnoreCase(String usernameFragment);

    List<User> findWithinDateRange(LocalDateTime start, LocalDateTime end);


}
