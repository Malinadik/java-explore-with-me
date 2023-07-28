package ru.practicum.exception;

public class NotSupportedStateException extends RuntimeException {
    public NotSupportedStateException(String message) {
        super(message);
    }
}
