package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.repository.InMemoryRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
public class UserRepositoryMemoryImpl extends InMemoryRepository<User> implements UserRepository {
    @Override
    public Optional<User> getByEmail(String email) {
        return objects.values()
                .stream()
                .filter(x -> x.getEmail().equals(email))
                .findFirst();
    }
}
