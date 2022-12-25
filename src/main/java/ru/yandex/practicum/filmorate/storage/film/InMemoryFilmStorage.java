package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements Storage<Integer, Film> {

    private Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public void put(Integer filmId, Film film) {
        films.put(filmId, film);
    }

    @Override
    public Film get(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public void delete(Integer filmId) {
        films.remove(filmId);
    }

    @Override
    public Collection<Film> getAll() {
        return films
                .values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getAll(Predicate<? super Film> p) {
        return films
                .values()
                .stream()
                .filter(p)
                .sorted(Comparator.comparingInt(Film::getId))
                .collect(Collectors.toList());
    }
}
