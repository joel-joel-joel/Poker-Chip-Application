package com.joelcode.pokerchipsapplication.exceptions.custom;

public class AlreadyInRoomException extends RuntimeException {
    public AlreadyInRoomException() {
        super();
    }
    public AlreadyInRoomException(String message) {
        super(message);
    }
    public AlreadyInRoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
