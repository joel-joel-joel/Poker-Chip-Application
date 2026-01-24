package com.joelcode.pokerchipsapplication.dto.response;

import java.util.UUID;

public class PlayerRoundState {
    private UUID playerId;
    private String username;
    private int chipBalance;
    private int currentHandBet;
    private String status;
    private boolean isAllIn;

    public PlayerRoundState(UUID playerId, String username, int chipBalance,
                           int currentHandBet, String status, boolean isAllIn) {
        this.playerId = playerId;
        this.username = username;
        this.chipBalance = chipBalance;
        this.currentHandBet = currentHandBet;
        this.status = status;
        this.isAllIn = isAllIn;
    }

    // Getters
    public UUID getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getChipBalance() {
        return chipBalance;
    }

    public int getCurrentHandBet() {
        return currentHandBet;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAllIn() {
        return isAllIn;
    }
}
