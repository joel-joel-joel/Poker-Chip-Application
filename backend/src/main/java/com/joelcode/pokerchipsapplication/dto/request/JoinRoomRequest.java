package com.joelcode.pokerchipsapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class JoinRoomRequest {
    @NotBlank (message = "Room code is required")
    @Size (min = 6, max = 6, message = "Room code must be 6 digits")
    private String roomCode;

    public JoinRoomRequest() {}

    public JoinRoomRequest(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() { return roomCode; }

    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
}
