package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.InMemoryFriendsStorage;
import ru.yandex.practicum.filmorate.storage.likes.InMemoryLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.LinkedList;

abstract class FilmorateAppControllerTest {

    protected static final int MAX_SIMPLE_FILMS = 10;
    protected static final int MAX_SIMPLE_USERS = 3;
    protected static LinkedList<Film> simpleFilms;
    protected static LinkedList<User> simpleUsers;
    protected UserService userService;
    protected FilmService filmService;
    protected InMemoryLikesStorage inMemoryLikesStorage;
    protected InMemoryFriendsStorage inMemoryFriendsStorage;
    protected InMemoryFilmStorage inMemoryFilmStorage;
    protected InMemoryUserStorage inMemoryUserStorage;


    abstract void getController();

    @BeforeEach
    void getService() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryLikesStorage = new InMemoryLikesStorage();
        inMemoryFriendsStorage = new InMemoryFriendsStorage();
        userService = new UserService(inMemoryUserStorage, inMemoryFriendsStorage);
        filmService = new FilmService(inMemoryFilmStorage, inMemoryLikesStorage, userService);
    }

    @BeforeAll
    static void getFilmsThatWillPassValidation() {
        simpleFilms = new LinkedList<>();
        for (int i = 0; i < MAX_SIMPLE_FILMS; i++) {
            simpleFilms.add(Film.builder()
                    .name("Film " + i)
                    .rate(i)
                    .description("Test description fo film " + i)
                    .duration(100 + i * 2)
                    .releaseDate(LocalDate.now().minusYears(i))
                    .build());
        }
    }

    @BeforeAll
    static void getUsersThatWillPassValidationAndLoveFilms() {
        simpleUsers = new LinkedList<>();
        for (int i = 0; i < MAX_SIMPLE_USERS; i++) {
            User tempUser = User.builder()
                    .name("User " + i)
                    .login("user0" + i)
                    .email("user" + i + "@yandex.ru")
                    .birthday(LocalDate.now().minusYears(18 - i).plusMonths(2 * i))
                    .build();

            simpleUsers.add(tempUser);
        }
    }
}
