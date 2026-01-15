package com.joelcode.pokerchipsapplication.exceptions.custom;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException() { super(); }
    public InvalidGameStateException(String message) { super(message); }
    public InvalidGameStateException(String message, Throwable cause) { super(message, cause); }
}
