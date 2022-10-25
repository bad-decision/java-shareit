package ru.practicum.shareit.common.exception;

public class ConflictWithExistException extends RuntimeException {
    public ConflictWithExistException(String message) {
        super(message);
    }
}
