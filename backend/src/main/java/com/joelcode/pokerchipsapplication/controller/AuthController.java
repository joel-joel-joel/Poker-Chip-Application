package com.joelcode.pokerchipsapplication.controller;

import com.joelcode.pokerchipsapplication.dto.request.RegisterRequest;
import com.joelcode.pokerchipsapplication.dto.response.AuthResponse;
import com.joelcode.pokerchipsapplication.entities.User;
import com.joelcode.pokerchipsapplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")

public class AuthController {
    @Autowired
    private UserService userService;

    //Register a new user - * POST /api/auth/register

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws Exception {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        String token = userService.authenticateUser(request.getUsername(), request.getPassword());

        AuthResponse response = new AuthResponse(token, "Bearer", user.getUsername(), user.getEmail());
        return ResponseEntity.ok(response);
    }

    // Login existing user - POST /api/auth/login
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username){
        boolean available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(userService.isUsernameAvailable(username));
    }

    // Check if email is available - GET /api/auth/check-email?email=john@example.com
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email){
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(userService.isEmailAvailable(email));
    }

}
