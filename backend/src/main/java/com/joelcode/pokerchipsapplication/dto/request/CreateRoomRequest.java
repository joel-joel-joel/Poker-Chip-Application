package com.joelcode.pokerchipsapplication.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class CreateRoomRequest {

    @NotBlank (message = "Room name is required")
    private String name;

    @NotNull (message = "Max players is required")
    @Min(value = 2, message = "Must have at least 2 players")
    @Max(value = 20, message = "Cannot have more than 20 players")
    private Integer maxPlayers;

    @NotNull (message = "Starting chips is required")
    @Min (value = 100, message = "Players must start with at least a 100 chips")
    private Integer startingChips;

    public CreateRoomRequest() {}

    public CreateRoomRequest(String name, Integer maxPlayers, Integer startingChips) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.startingChips = startingChips;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Integer getMaxPlayers() {return maxPlayers;}
    public void setMaxPlayers(Integer maxPlayers) {this.maxPlayers = maxPlayers;}

    public Integer getStartingChips() {return startingChips;}
    public void setStartingChips(Integer startingChips) {this.startingChips = startingChips;}

}
