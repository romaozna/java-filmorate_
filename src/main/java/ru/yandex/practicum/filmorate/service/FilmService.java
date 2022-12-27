package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestArgumentException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Storage<Integer, Film> filmStorage;
    private Storage<Integer, Set<User>> likesStorage;
    private UserService userService;
    private int id;

    @Autowired
    public FilmService(Storage<Integer, Film> filmStorage, @Qualifier("likes") Storage<Integer, Set<User>> likesStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.userService = userService;
        this.id = 0;
    }

    public Film get(int filmId) {
        return getFilmOrException(filmId);
    }

    public Film save(Film film) {
        validate(film);
        film.setId(++id);
        filmStorage.put(id, film);
        likesStorage.put(id, new HashSet<>());
        return film;
    }

    public Film update(Film film) {
        validate(film);
        int filmId = film.getId();
        getFilmOrException(filmId);
        filmStorage.delete(filmId);
        filmStorage.put(filmId, film);
        return film;
    }

    public void delete(int filmId) {
        getFilmOrException(filmId);
        filmStorage.delete(filmId);
        likesStorage.delete(filmId);

    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getAll());
    }

    public List<Film> getFilms(int year) {
        Predicate<Film> p = film -> (film.getReleaseDate().getYear() == year);
        return new ArrayList<>(filmStorage.getAll(p));
    }

    public List<Film> getFilms(String name) {
        Predicate<Film> p = film -> (film.getName().contains(name));
        return new ArrayList<>(filmStorage.getAll(p));
    }

    public List<Film> getFilms(int year, String name) {
        Predicate<Film> p = film -> (film.getReleaseDate().getYear() == year && film.getName().contains(name));
        return new ArrayList<>(filmStorage.getAll(p));
    }

    public void like(int userId, int filmId) {
        Film film = getFilmOrException(filmId);
        User savedUser = userService.get(userId);
        Set<User> thisFilmFans = new HashSet<>(likesStorage.get(filmId));
        if(savedUser != null) {
            if(!thisFilmFans.contains(savedUser)) {
                thisFilmFans.add(savedUser);
                likesStorage.put(filmId, thisFilmFans);
                film.setRate(film.getRate() + 1);
            }
        } else throw new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует");
    }

    public void unlike(int userId, int filmId) {
        Film film = getFilmOrException(filmId);
        User savedUser = userService.get(userId);
        Set<User> thisFilmFans = new HashSet<>(likesStorage.get(filmId));
        if(savedUser != null) {
            if(thisFilmFans.contains(savedUser)) {
                thisFilmFans.remove(savedUser);
                likesStorage.put(filmId, thisFilmFans);
                film.setRate(film.getRate() - 1);
            }
        } else throw new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует");
    }

    public List<Film> getPopularFilms(int topCount) {
        return filmStorage
                .getAll()
                .stream()
                .sorted(Comparator
                        .comparingInt(Film::getRate)
                        .reversed())
                .limit(topCount)
                .collect(Collectors.toList());
    }

    private Film getFilmOrException(int filmId) {
        Optional<Film> film = Optional.ofNullable(filmStorage.get(filmId));
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

