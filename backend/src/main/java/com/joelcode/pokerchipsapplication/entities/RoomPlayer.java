package com.joelcode.pokerchipsapplication.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "room_player")
public class RoomPlayer {

    public RoomPlayer(User user, Room room, int chipBalance, int position, PlayerStatus status) {
        this.user = user;
        this.room = room;
        this.chipBalance = chipBalance;
        this.position = position;
        this.status = status;
    }

    public RoomPlayer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "room")
    private Room room;

    private int chipBalance;

    private int position;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getChipBalance() {
        return chipBalance;
    }

    public void setChipBalance(int chipBalance) {
        this.chipBalance = chipBalance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PlayerStatus getPlayerStatus() {
        return status;
    }

    public void setPlayerStatus(PlayerStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    // Player status is now defined within RoomPlayer for cohesion
    public enum PlayerStatus {
        ACTIVE,
        INACTIVE
    }
}

