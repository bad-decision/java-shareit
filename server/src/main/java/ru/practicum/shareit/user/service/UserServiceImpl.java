package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.ConflictWithExistException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
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

        return userRepository.save(existUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        userRepository.deleteById(userId);
    }
}
