package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;

    @Test
    public void addItem_shouldAddNewItem() {
        User newUser = new User();
        newUser.setEmail("test@mail.ru");
        newUser.setName("test");

        userService.addUser(newUser);

        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setAvailable(true);
        itemAddDto.setDescription("test 2");
        itemAddDto.setName("test name");
        itemAddDto.setOwnerId(newUser.getId());

        Item addedItem = itemService.addItem(itemAddDto);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", addedItem.getId()).getSingleResult();

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(itemAddDto.getName(), item.getName());
        Assertions.assertEquals(itemAddDto.getDescription(), item.getDescription());
    }
}