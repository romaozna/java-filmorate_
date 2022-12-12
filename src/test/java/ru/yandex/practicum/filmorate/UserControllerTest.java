package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void getController() {
        userController = new UserController();
    }

    @Test
    void blankEmailValidationTest() {
        user = User.builder()
                .id(1)
                .email("")
                .login("Test")
                .name("Name")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(ex.getMessage(), "Неверный формат для электронной почты");
    }

    @Test
    void emailWithoutAtSignValidationTest() {
        user = User.builder()
                .id(1)
                .email("test.yandex.ru")
                .login("Test")
                .name("Name")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(ex.getMessage(), "Неверный формат для электронной почты");
    }

    @Test
    void blankLoginValidationTest() {
        user = User.builder()
                .id(1)
                .email("test@yandex.ru")
                .login("")
                .name("Name")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(ex.getMessage(), "Логин не может быть пустым или содержать пробелы");
    }

    @Test
    void loginWithSpacesValidationTest() {
        user = User.builder()
                .id(1)
                .email("test@yandex.ru")
                .login("User test")
                .name("Name")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(ex.getMessage(), "Логин не может быть пустым или содержать пробелы");
    }

    @Test
    void birthdayInFutureValidationTest() {
        user = User.builder()
                .id(1)
                .email("test@yandex.ru")
                .login("User")
                .name("Name")
                .birthday(LocalDate.now().plusYears(1))
                .build();

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(ex.getMessage(), "Дата рождения не может быть в будущем");
    }

    @Test
    void blankNameValidationTest() {
        user = User.builder()
                .id(1)
                .email("test@yandex.ru")
                .login("User")
                .name("")
                .birthday(LocalDate.now().minusYears(12))
                .build();

        assertTrue(user.getName().isBlank());

        userController.create(user);

        assertEquals(user.getName(), user.getLogin());
    }
}
