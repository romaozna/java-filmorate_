package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AppendException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;


import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FilmController {

    private final List<Film> films = new ArrayList<>();
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Запрошен список фильмов. В списке {} фильмов", films.size());
        return films;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        log.debug("PUT-запрос /films: {}", film);
        validate(film);
        Optional<Film> savedFilm = films
                .stream()
                .filter(f -> f.getId() == film.getId())
                .findFirst();
        if(savedFilm.isPresent()) {
            films.remove(savedFilm.get());
            films.add(film);
        } else throw new AppendException("Такого фильма не существует!");
        return film;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        log.debug("POST-запрос /films: {}", film);
        validate(film);
        film.setId(films.size() + 1);
        films.add(film);
        return film;
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