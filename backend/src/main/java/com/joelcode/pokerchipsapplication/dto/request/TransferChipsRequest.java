package com.joelcode.pokerchipsapplication.dto.request;

import com.joelcode.pokerchipsapplication.entities.ChipTransaction.transactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class TransferChipsRequest {
    @NotNull(message = "From player ID is required")
    private UUID fromPlayerId;

    @NotNull(message = "To player ID is required")
    private UUID toPlayerId;

    @Positive(message = "Amount must be positive")
    private int amount;

    @NotNull(message = "Transaction type is required")
    private transactionType type;

    // Constructors
    public TransferChipsRequest() {}

    public TransferChipsRequest(UUID fromPlayerId, UUID toPlayerId, int amount, transactionType type) {
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public UUID getFromPlayerId() { return fromPlayerId; }
    public void setFromPlayerId(UUID fromPlayerId) { this.fromPlayerId = fromPlayerId; }

    public UUID getToPlayerId() { return toPlayerId; }
    public void setToPlayerId(UUID toPlayerId) { this.toPlayerId = toPlayerId; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public transactionType getType() { return type; }
    public void setType(transactionType type) { this.type = type; }
}
