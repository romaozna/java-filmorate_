package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM MPA ORDER BY MPA_ID";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.query(sql, new MpaMapper(), id)
                .stream()
                .findAny()
                .orElse(null);
    }
}
