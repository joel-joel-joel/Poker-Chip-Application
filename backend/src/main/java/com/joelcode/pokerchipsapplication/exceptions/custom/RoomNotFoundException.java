package com.joelcode.pokerchipsapplication.exceptions.custom;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() { super(); }
    public RoomNotFoundException(String message) { super(message); }
    public RoomNotFoundException(String message, Throwable cause) { super(message, cause); }
}
