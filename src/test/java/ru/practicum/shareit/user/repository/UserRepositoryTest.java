package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    private User user1 = null;
    private User user2 = null;

    @BeforeEach
    public void prepare() {
        user1 = new User();
        user1.setName("test");
        user1.setEmail("test@mail.ru");

        user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@mail.ru");
    }

    @Test
    public void findAllWithEmptyRepository_shouldReturnEmpty() {
        List<User> users = repository.findAll();

        Assertions.assertEquals(0, users.size());
    }

    @Test
    public void getUserByEmail_shouldReturnCorrectUser() {
        user1 = repository.save(user1);
        user2 = repository.save(user2);

        Optional<User> foundUser = repository.getByEmail(user1.getEmail());
        Assertions.assertEquals(user1.getId(), foundUser.get().getId());
        Assertions.assertEquals(user1.getEmail(), foundUser.get().getEmail());
        Assertions.assertEquals(user1.getName(), foundUser.get().getName());
    }
}
