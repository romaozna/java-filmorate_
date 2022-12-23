package ru.yandex.practicum.filmorate.exceptions;

public class IllegalRequestArgumentException extends RuntimeException {

    public IllegalRequestArgumentException(final String message) {
        super(message);
    }

    public String getDetailMessage() {
        return getMessage();
    }
}
