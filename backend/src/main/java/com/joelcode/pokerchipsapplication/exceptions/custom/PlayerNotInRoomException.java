package com.joelcode.pokerchipsapplication.exceptions.custom;

public class PlayerNotInRoomException extends RuntimeException {
    public PlayerNotInRoomException() { super(); }
    public PlayerNotInRoomException(String message) { super(message); }
    public PlayerNotInRoomException(String message, Throwable cause) { super(message, cause); }
}
