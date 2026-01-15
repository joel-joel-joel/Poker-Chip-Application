package com.joelcode.pokerchipsapplication.service.events;

public class PlayerEvent {
    private String type;
    private String username;
    private int position;

    public PlayerEvent() {}

    public PlayerEvent(String type, String username, int position) {
        this.type = type;
        this.username = username;
        this.position = position;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
