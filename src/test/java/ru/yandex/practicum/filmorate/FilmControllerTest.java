package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void getController() {
        filmController = new FilmController();
    }

    @Test
    void blankFilmNameTest() {
        film = Film.builder()
                .name("")
                .id(1)
                .description("test description")
                .releaseDate(LocalDate.now().minusDays(30))
                .duration(100)
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(ex.getMessage(), "Название фильма не может быть пустым");
    }

    @Test
    void longFilmDescriptionTest() {
        String longDescription = fillString(201, 'r');
        film = Film.builder()
                .name("Test")
                .id(1)
                .description(longDescription)
                .releaseDate(LocalDate.now().minusDays(30))
                .duration(100)
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(ex.getMessage(), "Слишком длинное описание фильма");
    }

    @Test
    void releaseDateFilmTest() {
        film = Film.builder()
                .name("Test")
                .id(1)
                .description("Test description")
                .releaseDate(LocalDate.now().minusYears(150))
                .duration(100)
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(ex.getMessage(), "Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void negativeDurationFilmTest() {
        film = Film.builder()
                .name("Test")
                .id(1)
                .description("Test description")
                .releaseDate(LocalDate.now().minusYears(1))
                .duration(-1)
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(ex.getMessage(), "Продолжительность фильма должна быть положительной");
    }

    private String fillString(int count,char c) {
        StringBuilder sb = new StringBuilder(count);
        for(int i=0; i<count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
