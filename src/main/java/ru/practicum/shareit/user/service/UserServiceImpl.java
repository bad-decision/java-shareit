package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ConflictWithExistException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    @Override
    public User addUser(User user) {
        if (userRepository.getByEmail(user.getEmail()).isPresent())
            throw new ConflictWithExistException("User with this email already exist: " + user.getEmail());
        return userRepository.add(user);
    }

    @Override
    public User updateUser(User user) {
        User existUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + user.getId()));

        if (user.getEmail() != null && userRepository.getByEmail(user.getEmail()).isPresent())
            throw new ConflictWithExistException("User with this email already exist: " + user.getEmail());

        if (user.getName() != null)
            existUser.setName(user.getName());

        if (user.getEmail() != null)
            existUser.setEmail(user.getEmail());

        return userRepository.update(existUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        userRepository.deleteById(userId);
    }
}
