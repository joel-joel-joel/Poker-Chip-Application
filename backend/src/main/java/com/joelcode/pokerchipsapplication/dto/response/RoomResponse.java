package com.joelcode.pokerchipsapplication.dto.response;

import com.joelcode.pokerchipsapplication.entities.Room;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoomResponse {

    private UUID id;
    private String code;
    private String name;
    private String hostUsername;
    private Room.roomStatus roomStatus;
    private Integer maxPlayers;
    private Integer startingChips;
    private Integer currentPlayerCount;
    private LocalDateTime createdAt;

    public RoomResponse() {}

    public RoomResponse(Room room){
        this.id = room.getId();
        this.code = room.getCode();
        this.name = room.getRoomName();
        this.hostUsername = room.getHost() != null ? room.getHost().getUsername() : null;
        this.roomStatus = room.getStatus();
        this.maxPlayers = room.getMaxPlayers();
        this.startingChips = room.getStaringChips();
        this.currentPlayerCount = room.getRoomPlayers().size();
        this.createdAt = room.getCreatedAt();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHostUsername() { return hostUsername; }
    public void setHostUsername(String hostUsername) { this.hostUsername = hostUsername; }

    public Room.roomStatus getRoomStatus() { return roomStatus; }
    public void setRoomStatus(Room.roomStatus roomStatus) { this.roomStatus = roomStatus; }

    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(Integer maxPlayers) { this.maxPlayers = maxPlayers; }

    public Integer getStartingChips() { return startingChips; }
    public void setStartingChips(Integer startingChips) { this.startingChips = startingChips; }

    public Integer getCurrentPlayerCount() { return currentPlayerCount; }
    public void setCurrentPlayerCount(Integer currentPlayerCount) { this.currentPlayerCount = currentPlayerCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
