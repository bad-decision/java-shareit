package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class UserServiceTest {
    private UserService userService;
    private User user;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void prepare() {
        userService = new UserServiceImpl(userRepository);
        user = new User();
        user.setEmail("test@mail.ru");
        user.setName("test");
    }

    @Test
    public void addUser_shouldAddNewUser() {
        User newUser = new User();
        newUser.setEmail("test@mail.ru");
        newUser.setName("test");
        newUser.setId(1L);

        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .then((invocationOnMock) -> {
                    newUser.setId(1L);
                    return user;
                });
        User addedUser = userService.addUser(user);

        Assertions.assertEquals(user.getId(), addedUser.getId());
        Assertions.assertEquals(user.getName(), addedUser.getName());
        Assertions.assertEquals(user.getEmail(), addedUser.getEmail());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user);
    }

    @Test
    public void getUser_shouldReturnUser() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        User foundUser = userService.getUser(user.getId());

        Assertions.assertEquals(foundUser.getId(), user.getId());
        Assertions.assertEquals(foundUser.getName(), user.getName());
        Assertions.assertEquals(foundUser.getEmail(), user.getEmail());
    }

    @Test
    public void getNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    public void getUsers_shouldReturnUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<User> users = userService.getUsers();

        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(user.getId(), users.get(0).getId());
        Assertions.assertEquals(user.getName(), users.get(0).getName());
        Assertions.assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    public void deleteNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    public void deleteUser_shouldVerifyInvocation() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        userService.deleteUser(user.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(user.getId());
    }
}