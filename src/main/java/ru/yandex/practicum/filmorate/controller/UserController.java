package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAll(@RequestParam(required = false) Optional<String> name) {
        log.debug("Запрошен список пользователей GET ../users c параметром {}", name);
        if (name.isPresent()) {
            return userService.getUsers(name.get());
        }
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        log.debug("Запрошен пользователь с id={}", userId);
        return userService.get(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        log.debug("Запрошен список друзей пользователя с id={}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int friendId) {
        log.debug("Запрошен список общих друзей пользователя с id={} с пользователем с id={}", userId, friendId);
        return userService.getCommonFriends(userId, friendId);
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        log.debug("PUT-запрос /users: {}", user);
        userService.validate(user);
        return userService.update(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addToFriends(@PathVariable int userId, @PathVariable int friendId) {
        log.debug("Запрос на добавление в друзья id = {} от пользователя с id = {}", friendId, userId);
        userService.addToFriends(userId, friendId);
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        log.debug("POST-запрос /users: {}", user);
        userService.validate(user);
        return userService.save(user);
    }

    @DeleteMapping({"/{userId}"})
    public void deleteUser(@PathVariable int userId) {
        log.debug("Запрос на удаление пользователя с  id = {}", userId);
        userService.delete(userId);
    }

    @DeleteMapping({"/{userId}/friends/{friendId}"})
    public void deleteUser(@PathVariable int userId, @PathVariable int friendId) {
        log.debug("Запрос на удаление из друзей id = {} от пользователя с id = {}", friendId, userId);
        userService.deleteFromFriends(userId, friendId);
    }


}