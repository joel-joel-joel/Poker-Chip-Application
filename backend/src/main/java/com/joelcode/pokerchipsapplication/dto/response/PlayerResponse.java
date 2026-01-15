package com.joelcode.pokerchipsapplication.dto.response;

import com.joelcode.pokerchipsapplication.entities.RoomPlayer;

import java.util.UUID;

public class PlayerResponse {
    private UUID id;
    private String username;
    private Integer chipBalance;
    private Integer position;
    private RoomPlayer.PlayerStatus status;

    public PlayerResponse() {}

    public PlayerResponse(RoomPlayer roomPlayer) {
        this.id = roomPlayer.getId();
        this.username = roomPlayer.getUser().getUsername();
        this.chipBalance = roomPlayer.getChipBalance();
        this.position = roomPlayer.getPosition();
        this.status = roomPlayer.getStatus();
    }

    public PlayerResponse(PlayerResponse playerResponse) {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getChipBalance() { return chipBalance; }
    public void setChipBalance(Integer chipBalance) { this.chipBalance = chipBalance; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public RoomPlayer.PlayerStatus getStatus() { return status; }
    public void setStatus(RoomPlayer.PlayerStatus status) { this.status = status; }
}
