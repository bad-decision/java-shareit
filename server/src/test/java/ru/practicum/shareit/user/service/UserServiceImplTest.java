package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;
    private final EntityManager em;

    @Test
    public void addUser_shouldAddNewUser() {
        User newUser = new User();
        newUser.setEmail("test@mail.ru");
        newUser.setName("test");

        userService.addUser(newUser);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(newUser.getName(), user.getName());
        Assertions.assertEquals(newUser.getEmail(), user.getEmail());
    }

    @Test
    public void updateUser_shouldUpdateUser() {
        User newUser = new User();
        newUser.setEmail("test1@mail.ru");
        newUser.setName("test");
        userService.addUser(newUser);

        User updateUser = new User();
        updateUser.setEmail("updates@mail.ru");
        updateUser.setName("update");
        updateUser.setId(newUser.getId());

        userService.updateUser(updateUser);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(newUser.getName(), user.getName());
        Assertions.assertEquals(newUser.getEmail(), user.getEmail());
    }
}