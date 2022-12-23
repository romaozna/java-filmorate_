package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest extends FilmorateAppControllerTest {
    private FilmController filmController;
    private UserController userController;
    private Film film;

    @BeforeEach
    @Override
    void getController() {
        this.filmController = new FilmController(filmService);
        this.userController = new UserController(userService);
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

    @Test
    void getAllFilmsTest() {
        fillFilmStorageWithSimpleFilms();

        assertEquals(MAX_SIMPLE_FILMS, filmController.findAll(Optional.empty(), Optional.empty()).size());
        assertEquals(MAX_SIMPLE_FILMS, filmController.findAll(Optional.empty(), Optional.of("Film")).size());
        assertEquals(1, filmController.findAll(Optional.of(LocalDate.now().getYear()), Optional.empty()).size());
        assertEquals(1, filmController.findAll(Optional.of(LocalDate.now().getYear()), Optional.of("Film")).size());
    }

    @Test
    void getFilmByIdTest() {
        filmController.create(simpleFilms.get(0));

        assertEquals(simpleFilms.get(0), filmController.getFilmById(1));
    }

    @Test
    void setLikeUnlikeAndGetPopularFilms() {
        fillFilmStorageWithSimpleFilms();
        userController.create(simpleUsers.get(0));

        List<Film> top = filmController.getTopFilms(5);

        assertEquals(5, top.size());

        Film savedFilm = top.get(0);
        int savedRate = savedFilm.getRate();

        assertEquals(simpleFilms.get(MAX_SIMPLE_FILMS - 1).getRate(), savedRate);

        filmController.likeFilm(1, savedFilm.getId());

        assertNotEquals(savedRate, savedFilm.getRate());

        filmController.likeFilm(1, savedFilm.getId());
        filmController.likeFilm(1, savedFilm.getId());
        filmController.likeFilm(1, savedFilm.getId());
        filmController.likeFilm(1, savedFilm.getId());

        assertEquals(savedRate + 1, savedFilm.getRate());

        filmController.unlikeFilm(1, savedFilm.getId());
        filmController.unlikeFilm(1, savedFilm.getId());

        assertEquals(savedRate, savedFilm.getRate());
    }

    private String fillString(int count,char c) {
        StringBuilder sb = new StringBuilder(count);
        for(int i=0; i<count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private void fillFilmStorageWithSimpleFilms() {
        for (Film film : simpleFilms) {
            filmController.create(film);
        }
    }

}
