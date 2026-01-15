package com.joelcode.pokerchipsapplication.service.events;

import com.joelcode.pokerchipsapplication.entities.RoomPlayer;

import java.util.List;

public class GameEndedEvent {
    private String roomCode;
    private List<RoomPlayer> finalStandings;

    public GameEndedEvent() {}

    public GameEndedEvent(String roomCode, List<RoomPlayer> finalStandings) {
        this.roomCode = roomCode;
        this.finalStandings = finalStandings;
    }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public List<RoomPlayer> getFinalStandings() { return finalStandings; }
    public void setFinalStandings(List<RoomPlayer> finalStandings) { this.finalStandings = finalStandings; }
}
