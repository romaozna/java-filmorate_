package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest extends FilmorateAppControllerTest{
    private UserController userController;
    private User user;

    @BeforeEach
    @Override
    void getController() {
        userController = new UserController(userService);
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

    @Test
    void getCommonFriendTest() {
        fillUserStorageWithSimpleUsers();

        assertEquals(MAX_SIMPLE_USERS, userController.findAll(Optional.empty()).size());

        final User firstUser = userController.getUser(1);
        final User secondUser = userController.getUser(2);
        final User thirdUser = userController.getUser(3);

        userController.addToFriends(firstUser.getId(), secondUser.getId());
        userController.addToFriends(thirdUser.getId(), secondUser.getId());

        assertEquals(1, userController.getCommonFriends(firstUser.getId(), thirdUser.getId()).size());
        assertEquals(secondUser, userController.getCommonFriends(firstUser.getId(), thirdUser.getId()).get(0));
    }

    private void fillUserStorageWithSimpleUsers() {
        for (User user : simpleUsers) {
            userController.create(user);
        }
    }

}
