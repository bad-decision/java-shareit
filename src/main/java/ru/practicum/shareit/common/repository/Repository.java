package ru.practicum.shareit.common.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T add(T item);
    T update(T item);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
