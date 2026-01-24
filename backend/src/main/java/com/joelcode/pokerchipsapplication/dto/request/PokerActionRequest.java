package com.joelcode.pokerchipsapplication.dto.request;

import java.util.UUID;

public class PokerActionRequest {

    public enum ActionType {
        BET, CALL, RAISE, CHECK, FOLD, ALL_IN
    }

    private UUID roundId;
    private UUID playerId;
    private ActionType action;
    private Integer amount;  // Required for BET, RAISE; optional otherwise

    // Getters and setters
    public UUID getRoundId() {
        return roundId;
    }

    public void setRoundId(UUID roundId) {
        this.roundId = roundId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
