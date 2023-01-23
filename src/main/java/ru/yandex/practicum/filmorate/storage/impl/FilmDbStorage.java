package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreStorage genreStorage;

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID";
        return jdbcTemplate.query(sql, this::filmMapper);
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE f.FILM_ID = ?";
        return jdbcTemplate.query(sql, this::filmMapper, id)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("FILM_NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("MPA_ID", film.getMpa().getId());
        int filmId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();

        updateFilmGenres(film.getGenres(), filmId);

        return getById(filmId);
    }

    @Override
    public Film update(Film film) {

        String sql = "UPDATE FILMS " +
                "SET " +
                "FILM_NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());

        updateFilmGenres(film.getGenres(), film.getId());

        return getById(film.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT " +
                "f.FILM_ID, " +
                "f.FILM_NAME, " +
                "f.DESCRIPTION, " +
                "f.RELEASE_DATE, " +
                "f.DURATION, " +
                "m.MPA_ID AS MPA_ID, " +
                "m.MPA_NAME AS MPA_NAME, " +
                "g.GENRE_ID, " +
                "g.NAME AS NAME, " +
                "COUNT(l.FILM_ID) AS likes " +
                "FROM FILMS AS f " +
                "INNER JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN LIKES AS l ON f.FILM_ID = l.FILM_ID " +
                "GROUP BY f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, m.MPA_ID, m.MPA_NAME, g.GENRE_ID, g.NAME " +
                "ORDER BY likes DESC, f.FILM_NAME " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::filmMapper, count);
    }

    @Override
    public void likeFilm(int filmId, int userId) {
        String sql = "INSERT INTO LIKES VALUES(?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLikeFromFilm(int filmId, int userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    private void updateFilmGenres(List<Genre> genres, int filmId) {
        if (genres == null) {
            return;
        }

        List<Integer> genreUniqueIds = genres.stream()
                .map(Genre::getId)
                .distinct()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "INSERT INTO FILMS_GENRES VALUES(?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        int genreId = genreUniqueIds.get(i);
                        ps.setInt(1, filmId);
                        ps.setInt(2, genreId);
                    }

                    public int getBatchSize() {
                        return genreUniqueIds.size();
                    }
                });
    }

    private Film filmMapper(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")))
                .genres(genreStorage.getByFilmId(rs.getInt("FILM_ID")))
                .build();
    }
}
