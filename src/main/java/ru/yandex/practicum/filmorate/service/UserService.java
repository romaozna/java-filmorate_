package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestArgumentException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
public class UserService {

    private Storage<Integer, User> userStorage;
    private Storage<Integer, Set<User>> friendsStorage;

    private int id;

    @Autowired
    public UserService(Storage<Integer, User> userStorage, @Qualifier("friends") Storage<Integer, Set<User>> friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.id = 0;
    }

    public User get(int userId) {
        return getUserOrException(userId);
    }

    public User save(User user) {
        validate(user);
        user.setId(++id);
        userStorage.put(id, user);
        friendsStorage.put(id, new HashSet<>());
        return user;
    }

    public User update(User user) {
        validate(user);
        int userId = user.getId();
        getUserOrException(userId);
        userStorage.delete(userId);
        userStorage.put(userId, user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getAll());
    }

    public List<User> getUsers(String name) {
        Predicate<User> p = user -> (user.getName().contains(name));
        return new ArrayList<>(userStorage.getAll(p));
    }

    public void delete(int userId) {
        getUserOrException(userId);
        userStorage.delete(userId);
        friendsStorage.delete(userId);
    }

    public void addToFriends(int userId, int friendId) {
        User user = getUserOrException(userId);
        User friend = getUserOrException(friendId);
        Set<User> friendList = new HashSet<>(friendsStorage.get(userId));
        friendList.add(friend);
        friendsStorage.put(userId, friendList);
        friendList = friendsStorage.get(friendId);
        friendList.add(user);
        friendsStorage.put(friendId, friendList);
    }

    public void deleteFromFriends(int userId, int friendId) {
        User user = getUserOrException(userId);
        User friend = getUserOrException(friendId);
        Set<User> friendList = new HashSet<>(friendsStorage.get(userId));
        friendList.remove(friend);
        friendsStorage.put(userId, friendList);
        friendList = friendsStorage.get(friendId);
        friendList.remove(user);
        friendsStorage.put(friendId, friendList);
    }

    public List<User> getFriends(int userId) {
        getUserOrException(userId);
        return new ArrayList<>(friendsStorage.get(userId));
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> userFriends = new ArrayList<>(getFriends(userId));
        List<User> otherUserFriends = new ArrayList<>(getFriends(otherUserId));
        userFriends.retainAll(otherUserFriends);
        return  userFriends;
    }

    private User getUserOrException(int userId) {
        Optional<User> user = Optional.ofNullable(userStorage.get(userId));
        if(user.isEmpty()) {
            throw new IllegalRequestArgumentException("Пользователя с id=" + userId + " не существует");
        }
        return user.get();
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
