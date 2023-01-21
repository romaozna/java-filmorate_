package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestArgumentException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User get(int userId) {
        return getUserOrException(userId);
    }

    public User save(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        int userId = user.getId();
        getUserOrException(userId);
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getAll());
    }

    public void delete(int userId) {
        getUserOrException(userId);
        userStorage.delete(userId);
    }

    public void addToFriends(int userId, int friendId) {
        getUserOrException(userId);
        getUserOrException(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFromFriends(int userId, int friendId) {
        getUserOrException(userId);
        getUserOrException(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getUserOrException(userId);
        return new ArrayList<>(userStorage.getFriends(userId));
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        getUserOrException(userId);
        getUserOrException(otherUserId);
        return new ArrayList<>(userStorage.getCommonFriends(userId, otherUserId));
    }

    private User getUserOrException(int userId) {
        return Optional.ofNullable(userStorage.getById(userId))
                .orElseThrow(() -> new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует"));
    }

    private void validate(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.debug("Неверный формат email: {}", user.getEmail());
            throw new ValidationException("Неверный формат для электронной почты");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("Логин не может быть пустым или содержать пробелы: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Дата рождения должна быть раньше текущей даты: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Пустое поле имени. Имя = Логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
