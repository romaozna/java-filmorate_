package ru.yandex.practicum.filmorate.exceptions;

public class AppendException extends RuntimeException {

    public AppendException(final String message) {
        super(message);
    }

    public String getDetailMessage() {
        return getMessage();
    }
}
