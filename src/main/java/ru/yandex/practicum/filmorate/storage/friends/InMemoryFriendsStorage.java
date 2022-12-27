package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Qualifier("friends")
public class InMemoryFriendsStorage implements Storage<Integer, Set<User>> {

    private Map<Integer, Set<User>> friends;

    public InMemoryFriendsStorage() {
        this.friends = new HashMap<>();
    }

    @Override
    public void put(Integer userId, Set<User> friendList) {
        friends.put(userId, friendList);
    }

    @Override
    public Set<User> get(Integer userId) {
        return friends
                .get(userId)
                .stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void delete(Integer userId) {
        friends.remove(userId);
    }

    @Override
    public Collection<Set<User>> getAll() {
        return friends.values();
    }

    @Override
    public Collection<Set<User>> getAll(Predicate<? super Set<User>> p) {
        return friends
                .values()
                .stream()
                .filter(p)
                .collect(Collectors.toList());
    }
}
