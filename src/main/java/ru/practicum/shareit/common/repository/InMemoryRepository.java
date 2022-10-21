package ru.practicum.shareit.common.repository;

import ru.practicum.shareit.common.model.Entity;

import java.util.*;

public class InMemoryRepository<T extends Entity<Long>> implements Repository<T, Long> {
    private Long id = 0L;
    protected final Map<Long, T> objects = new HashMap<>();

    @Override
    public T add(T item) {
        item.setId(++id);
        objects.put(id, item);
        return item;
    }

    @Override
    public T update(T item) {
        objects.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<T> findById(Long id) {
        if (objects.containsKey(id))
            return Optional.of(objects.get(id));
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return objects.containsKey(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(objects.values());
    }

    @Override
    public void deleteById(Long id) {
        objects.remove(id);
    }
}
