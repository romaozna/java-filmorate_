package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, new UserMapper(), id)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("EMAIL", user.getEmail())
                .addValue("USER_LOGIN", user.getLogin())
                .addValue("USER_NAME", user.getName())
                .addValue("BIRTHDAY", user.getBirthday());
        int userId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();

        return getById(userId);
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS " +
                "SET " +
                "EMAIL = ?, " +
                "USER_LOGIN = ?, " +
                "USER_NAME = ?, " +
                "BIRTHDAY = ? " +
                "WHERE USER_ID = ?";

        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return getById(user.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FRIENDSHIP");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("USER_ID", userId)
                .addValue("FRIEND_ID", friendId);
        simpleJdbcInsert.execute(parameters);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE " +
                "FROM FRIENDSHIP " +
                "WHERE " +
                "USER_ID = ? AND FRIEND_ID = ? ";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        String sql = "SELECT " +
                "u.USER_ID, " +
                "u.EMAIL, " +
                "u.USER_LOGIN, " +
                "u.USER_NAME, " +
                "u.BIRTHDAY " +
                "FROM FRIENDSHIP AS f " +
                "JOIN USERS AS u on u.USER_ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ?";

        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sql = "SELECT " +
                "u.USER_ID, " +
                "u.EMAIL, " +
                "u.USER_LOGIN, " +
                "u.USER_NAME, " +
                "u.BIRTHDAY " +
                "FROM FRIENDSHIP AS f " +
                "JOIN USERS AS u on u.USER_ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ? AND " +
                "f.FRIEND_ID IN (" +
                "SELECT FRIEND_ID " +
                "FROM FRIENDSHIP " +
                "WHERE USER_ID = ?)";

        return jdbcTemplate.query(sql, new UserMapper(), userId, otherUserId);
    }
}
