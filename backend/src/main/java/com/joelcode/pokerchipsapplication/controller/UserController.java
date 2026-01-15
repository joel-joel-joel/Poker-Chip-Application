package com.joelcode.pokerchipsapplication.controller;

import com.joelcode.pokerchipsapplication.entities.User;
import com.joelcode.pokerchipsapplication.security.UserPrincipal;
import com.joelcode.pokerchipsapplication.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")

public class UserController{
    @Autowired
    private UserService userService;

    // Get current user profile - GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findById(principal.getUser().getId());
        return ResponseEntity.ok(user);
    }

    // Get user by ID - GET /api/users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@org.springframework.web.bind.annotation.PathVariable UUID userId){
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    // Get user by username - GET /api/users/username/{username}
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@org.springframework.web.bind.annotation.PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    // Search users by username fragment - GET /api/users/search?query=john
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.findbyUsernameContainingIgnoreCase(query);
        return ResponseEntity.ok(users);
    }

    // Update user email - PUT /api/users/me/email
    @PutMapping("/me/email")
    public ResponseEntity<Void> updateEmail(
            @RequestParam String newEmail,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        userService.updateUserEmail(principal.getUser().getId(), newEmail);
        return ResponseEntity.ok().build();
    }

    // Update user password - PUT /api/users/me/password
    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        userService.updateUserPassword(principal.getUser().getId(), newPassword);
        return ResponseEntity.ok().build();
    }

    // Delete user account - DELETE /api/users/me
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        userService.deleteUser(principal.getUser().getId());
        return ResponseEntity.ok().build();
    }

}

