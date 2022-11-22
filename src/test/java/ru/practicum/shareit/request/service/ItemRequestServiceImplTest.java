package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final EntityManager em;

    @Test
    public void addItemRequest_shouldAddNewItemRequest() {
        User owner = new User();
        owner.setEmail("test@mail.ru");
        owner.setName("test");

        User booker = new User();
        booker.setEmail("test2@mail.ru");
        booker.setName("test2");

        userService.addUser(owner);
        userService.addUser(booker);

        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setAvailable(true);
        itemAddDto.setDescription("test 2");
        itemAddDto.setName("test name");
        itemAddDto.setOwnerId(owner.getId());

        itemService.addItem(itemAddDto);

        ItemRequestAddDto itemRequestAddDto = new ItemRequestAddDto();
        itemRequestAddDto.setDescription("test desc");
        itemRequestAddDto.setItemRequestOwnerId(booker.getId());
        ItemRequestDto addItemRequest = itemRequestService.addItemRequest(itemRequestAddDto);

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", addItemRequest.getId()).getSingleResult();

        Assertions.assertNotNull(itemRequest.getId());
        Assertions.assertEquals(itemRequestAddDto.getDescription(), addItemRequest.getDescription());
    }
}