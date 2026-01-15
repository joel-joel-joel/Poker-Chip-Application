package com.joelcode.pokerchipsapplication.service;


import com.joelcode.pokerchipsapplication.entities.User;
import com.joelcode.pokerchipsapplication.repositories.UserRepo;
import com.joelcode.pokerchipsapplication.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // ==== USER CREATION & AUTHENTICATION ====

    public User createUser(String username, String email, String password) throws Exception {
        if (userRepo.existsByUsername(username)){
            throw new Exception("Username already exists");
        }

        if (userRepo.existsByEmail(email)){
            throw new Exception("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepo.save(user);
    }

    public String authenticateUser(String username, String password){
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        return tokenProvider.generateToken(user);
    }

    // ==== USER LOOKUP METHODS ====

    public User findById(UUID id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    // ==== USER SEARCH & ANALYTICS ====

    public List<User> findbyUsernameContainingIgnoreCase(String usernameFragment){
        return userRepo.findByUsernameContainingIgnoreCase(usernameFragment);
    }

    public List<User> findRecentUsers(LocalDateTime since){
        return userRepo.findByCreatedAtAfter(since);
    }

    public List<User> findWithinDateRange(LocalDateTime start, LocalDateTime end){
        return userRepo.findWithinDateRange(start, end);
    }

    public List<User> findAfterDate(LocalDateTime date){
        return userRepo.findByCreatedAtAfter(date);
    }


    // ==== USER MANAGEMENT ====

    public void updateUserEmail(UUID id, String newEmail){
        User user = findById(id);

        if (userRepo.existsByEmail(newEmail)){
            throw (new RuntimeException("Email already exists"));
        }

        user.setEmail(newEmail);
        userRepo.save(user);
    }

    public void updateUserPassword(UUID id, String newPassword){
        User user = findById(id);
        user.setPassword(newPassword);
        userRepo.save(user);
    }

    public boolean isEmailAvailable(String email){
        return !userRepo.existsByEmail(email);
    }

    public boolean isUsernameAvailable(String username){
        return !userRepo.existsByUsername(username);
    }

    public void deleteUser(UUID id){
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.deleteById(id);
    }

}
