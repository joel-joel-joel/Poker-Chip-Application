package com.joelcode.pokerchipsapplication.dto.response;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameStateResponse {
    private UUID roomID;
    private String roomCode;
    private String roomName;
    private String roomStatus;
    private String hostUsername;
    private List<PlayerResponse> players;

    public GameStateResponse() {}

    public GameStateResponse(UUID roomID, String roomCode, String roomName, String roomStatus, String hostUsername, List<PlayerResponse> players) {
        this.roomID = roomID;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.roomStatus = roomStatus;
        this.hostUsername = hostUsername;
        // Takes all players as argument in PlayerResponse and transforms it into a list
        this.players = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    public UUID getRoomID() { return roomID; }
    public void setRoomID(UUID roomID) { this.roomID = roomID; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }

    public String getHostUsername() { return hostUsername; }
    public void setHostUsername(String hostUsername) { this.hostUsername = hostUsername; }

    public List<PlayerResponse> getPlayers() { return players; }
    public void setPlayers(List<PlayerResponse> players) { this.players = players; }


}
