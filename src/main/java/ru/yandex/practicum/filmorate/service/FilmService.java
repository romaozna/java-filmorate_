package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestArgumentException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film get(int filmId) {
        return getFilmOrException(filmId);
    }

    public Film save(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        getFilmOrException(film.getId());
        return filmStorage.update(film);
    }

    public void delete(int filmId) {
        getFilmOrException(filmId);
        filmStorage.delete(filmId);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getAll());
    }

    public void like(int userId, int filmId) {
        getFilmOrException(filmId);
        User savedUser = userService.get(userId);
        if(savedUser != null) {
            filmStorage.likeFilm(filmId, userId);
        } else throw new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует");
    }

    public void unlike(int userId, int filmId) {
        getFilmOrException(filmId);
        User savedUser = userService.get(userId);
        if(savedUser != null) {
            filmStorage.deleteLikeFromFilm(filmId, userId);
        } else throw new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует");
    }

    public List<Film> getPopularFilms(int topCount) {
        return filmStorage.getPopularFilms(topCount);
    }

    private Film getFilmOrException(int filmId) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getById(filmId));
        if(film.isEmpty()) {
            throw new IllegalRequestArgumentException("Фильма с id=" + filmId + " не существует");
        }
        return film.get();
    }

    private void validate(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Название фильма пустое: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Описание фильма слишком длинное: {}", film.getName().length());
            throw new ValidationException("Слишком длинное описание фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28", dateTimeFormatter))) {
            log.debug("Дата релиза раньше нижней границы: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.debug("Продолжительность фильма должна быть положительной: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}

