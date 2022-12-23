package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.function.Predicate;

@Component
public class InMemoryUserStorage implements Storage<Integer, User> {

    private Map<Integer, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public void put(Integer userId, User user) {
        users.put(userId, user);
    }

    @Override
    public User get(Integer userId) {
        return users.get(userId);
    }


    @Override
    public void delete(Integer userId) {
        users.remove(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Collection<User> getAll(Predicate<? super User> p) {
        Collection<User> usersByCondition = new ArrayList<>();
        for (User user : users.values()) {
            if (p.test(user)) {
                usersByCondition.add(user);
            }
        }
        return usersByCondition;
    }
}
