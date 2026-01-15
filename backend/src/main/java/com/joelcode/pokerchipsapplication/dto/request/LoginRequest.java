package com.joelcode.pokerchipsapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank (message = "Password is required")
    private String password;


    public void LoginRequest() {}

    public void LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
