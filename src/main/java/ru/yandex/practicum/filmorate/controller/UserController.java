package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;


import ru.yandex.practicum.filmorate.exceptions.AppendException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class UserController {

    private final List<User> users = new ArrayList<>();
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/users")
    public List<User> findAll() {
        log.debug("Запрошен список пользователей. В списке {} пользователей", users.size());
        return users;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        log.debug("PUT-запрос /users: {}", user);
        validate(user);
        Optional<User> savedUser = users
                .stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst();
        if(savedUser.isPresent()) {
            users.remove(savedUser.get());
            users.add(user);
        } else throw new AppendException("Такого пользователя не существует!");
        return user;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.debug("POST-запрос /users: {}", user);
        validate(user);
        user.setId(users.size() + 1);
        users.add(user);
        return user;
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