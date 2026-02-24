package com.joelcode.pokerchipsapplication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.smartcardio.Card;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
//Stores basic information on User, as well as the list of room players in the game
public class User {
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 225)
    private String username;

    @Column(unique = true, nullable = false, length = 225)
    private String email;

    @Column (nullable = false, length = 225)
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;


    // Maps to variable user in RoomPlayer entity to retrieve user ingame information
    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // Prevent circular reference during JSON serialization
    private List<RoomPlayer> roomPlayers = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoomPlayer> getRoomPlayers() {
        return roomPlayers;
    }


}
