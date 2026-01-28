package com.joelcode.pokerchipsapplication.dto.response;

import com.joelcode.pokerchipsapplication.entities.ChipTransaction;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChipTransactionDTO {
    private UUID id;
    private String transactionType;
    private int chipsAmount;
    private String fromPlayerUsername;
    private String toPlayerUsername;
    private LocalDateTime createdAt;
    private String description;

    public ChipTransactionDTO() {}

    public ChipTransactionDTO(ChipTransaction transaction) {
        this.id = transaction.getId();
        this.transactionType = transaction.getTransactionType().name();
        this.chipsAmount = transaction.getChipsAmount();
        this.fromPlayerUsername = transaction.getFromPlayer() != null ?
            transaction.getFromPlayer().getUser().getUsername() : null;
        this.toPlayerUsername = transaction.getToPlayer() != null ?
            transaction.getToPlayer().getUser().getUsername() : null;
        this.createdAt = transaction.getCreatedAt();
        this.description = transaction.getDescription();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getChipsAmount() {
        return chipsAmount;
    }

    public void setChipsAmount(int chipsAmount) {
        this.chipsAmount = chipsAmount;
    }

    public String getFromPlayerUsername() {
        return fromPlayerUsername;
    }

    public void setFromPlayerUsername(String fromPlayerUsername) {
        this.fromPlayerUsername = fromPlayerUsername;
    }

    public String getToPlayerUsername() {
        return toPlayerUsername;
    }

    public void setToPlayerUsername(String toPlayerUsername) {
        this.toPlayerUsername = toPlayerUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
