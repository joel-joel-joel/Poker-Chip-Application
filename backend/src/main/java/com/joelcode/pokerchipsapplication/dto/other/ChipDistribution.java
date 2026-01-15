package com.joelcode.pokerchipsapplication.dto.other;

import java.util.UUID;

public class ChipDistribution {
    private UUID toPlayerID;
    private Integer amount;

    public ChipDistribution() {}

    public ChipDistribution(UUID toPlayerID, Integer amount) {}

    public UUID getToPlayerID() {return toPlayerID;}
    public void setToPlayerID(UUID toPlayerID) {this.toPlayerID = toPlayerID;}

    public Integer getAmount() {return amount;}
    public void setAmount(Integer amount) {this.amount = amount;}
}
