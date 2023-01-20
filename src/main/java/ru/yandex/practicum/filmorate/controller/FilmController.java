package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;

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
    public List<Film> findAll() {
        log.debug("Запрошен список фильмов GET ../films с");
        return filmService.getFilms();
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

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("POST-запрос /films: {}", film);
        return filmService.save(film);
    }


}