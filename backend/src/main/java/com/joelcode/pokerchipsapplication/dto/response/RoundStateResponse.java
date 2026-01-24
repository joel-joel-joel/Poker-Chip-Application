package com.joelcode.pokerchipsapplication.dto.response;

import java.util.List;
import java.util.UUID;

public class RoundStateResponse {
    private UUID roundId;
    private int roundNumber;
    private int potAmount;
    private int currentBet;
    private List<PlayerRoundState> players;
    private String roundStatus;

    public RoundStateResponse(UUID roundId, int roundNumber, int potAmount,
                             int currentBet, List<PlayerRoundState> players, String roundStatus) {
        this.roundId = roundId;
        this.roundNumber = roundNumber;
        this.potAmount = potAmount;
        this.currentBet = currentBet;
        this.players = players;
        this.roundStatus = roundStatus;
    }

    // Getters
    public UUID getRoundId() {
        return roundId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getPotAmount() {
        return potAmount;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public List<PlayerRoundState> getPlayers() {
        return players;
    }

    public String getRoundStatus() {
        return roundStatus;
    }
}
