package com.joelcode.pokerchipsapplication.exceptions.custom;

public class RoomFullException extends RuntimeException {
    public RoomFullException() { super(); }
    public RoomFullException(String message) { super(message); }
    public RoomFullException(String message, Throwable cause) { super(message, cause); }
}
