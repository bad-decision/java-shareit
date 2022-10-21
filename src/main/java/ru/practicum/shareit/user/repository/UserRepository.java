package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.common.repository.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> getByEmail(String email);
}
