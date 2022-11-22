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
}