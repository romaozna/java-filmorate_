package ru.yandex.practicum.filmorate.storage;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.function.Predicate;

@Repository
public interface Storage<K, V> {

    void put(K key, V value);

    @Nullable V get(K key);

    void delete(K key);

    Collection<V> getAll();

    Collection<V> getAll(Predicate<? super V> p);
}
