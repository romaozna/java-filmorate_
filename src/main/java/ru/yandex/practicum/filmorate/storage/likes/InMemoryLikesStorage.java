package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.function.Predicate;

@Component
@Qualifier("likes")
public class InMemoryLikesStorage implements Storage<Integer, Set<User>> {

    private Map<Integer, Set<User>> likes;

    public InMemoryLikesStorage() {
        this.likes = new HashMap<>();
    }

    @Override
    public void put(Integer filmId, Set<User> likeList) {
        likes.put(filmId, likeList);
    }

    @Override
    public Set<User> get(Integer filmId) {
        return likes.get(filmId);
    }

    @Override
    public void delete(Integer filmId) {
        likes.remove(filmId);
    }

    @Override
    public Collection<Set<User>> getAll() {
        return likes.values();
    }

    @Override
    public Collection<Set<User>> getAll(Predicate<? super Set<User>> p) {
        Collection<Set<User>> likesByCondition = new ArrayList<>();
        for (Set<User> likeList : likes.values()) {
            if (p.test(likeList)) {
                likesByCondition.add(likeList);
            }
        }
        return likesByCondition;
    }

}
