package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {

    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll(
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> name) {

        log.debug("Запрошен список фильмов GET ../films с параметрами {}, {}", year, name);
        if (year.isEmpty() && name.isEmpty()) {
            return filmService.getFilms();
        } else if (year.isPresent() && name.isPresent()) {
            return filmService.getFilms(year.get(), name.get());
        } else if (year.isPresent()) {
            return filmService.getFilms(year.get());
        } else {
            return filmService.getFilms(name.get());
        }
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        log.debug("Запрошен фильм с id={}", filmId);
        return filmService.get(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрошен список из {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("PUT-запрос /films: {}", film);
        filmService.validate(film);
        return filmService.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable int userId, @PathVariable int filmId) {
        log.debug("Пользователь с id={} ставит лайк фильму с id={}", userId, filmId);
        filmService.like(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlikeFilm(@PathVariable int userId, @PathVariable int filmId) {
        log.debug("Пользователь с id={} забирает свой лайк к фильму с id={}", userId, filmId);
        filmService.unlike(userId, filmId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.debug("Запрос на удаление фильма с id={}", filmId);
        filmService.delete(filmId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("POST-запрос /films: {}", film);
        filmService.validate(film);
        return filmService.save(film);
    }


}