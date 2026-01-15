package com.joelcode.pokerchipsapplication.exceptions.custom;

public class InsufficientPlayersException extends RuntimeException {
    public InsufficientPlayersException() { super(); }
    public InsufficientPlayersException(String message) { super(message); }
    public InsufficientPlayersException(String message, Throwable cause) { super(message, cause); }
}
