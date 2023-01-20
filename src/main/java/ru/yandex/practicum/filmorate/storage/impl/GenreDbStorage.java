package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public List<Genre> getByFilmId(int filmId) {
        String sql = "SELECT fg.GENRE_ID AS GENRE_ID, g.NAME AS NAME " +
                "FROM FILMS_GENRES AS fg " +
                "JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";

        return jdbcTemplate.query(sql, new GenreMapper(), filmId);
    }

    @Override
    public Genre getById(int id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        return jdbcTemplate.query(sql, new GenreMapper(), id)
                .stream()
                .findAny()
                .orElse(null);
    }
}
