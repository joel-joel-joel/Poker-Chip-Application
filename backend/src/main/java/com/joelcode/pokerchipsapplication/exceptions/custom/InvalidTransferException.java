package com.joelcode.pokerchipsapplication.exceptions.custom;

public class InvalidTransferException extends RuntimeException {
    public InvalidTransferException() { super(); }
    public InvalidTransferException(String message) { super(message); }
    public InvalidTransferException(String message, Throwable cause) { super(message, cause); }
}
