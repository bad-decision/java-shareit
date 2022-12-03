package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private ItemRequest itemRequest1;

    @BeforeEach
    public void prepare() {
        user1 = new User();
        user1.setName("test");
        user1.setEmail("test@mail.ru");

        User user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@mail.ru");

        User user3 = new User();
        user3.setName("test3");
        user3.setEmail("test3@mail.ru");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        itemRequest1 = new ItemRequest();
        itemRequest1.setOwner(user1);
        itemRequest1.setDescription("test 1");
        itemRequest1.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setOwner(user2);
        itemRequest2.setDescription("test 2");
        itemRequest2.setCreated(LocalDateTime.now());

        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setOwner(user3);
        itemRequest3.setDescription("test 3");
        itemRequest3.setCreated(LocalDateTime.now());

        repository.save(itemRequest1);
        repository.save(itemRequest2);
        repository.save(itemRequest3);
    }

    @Test
    public void getItemRequestsByOwner_shouldReturnCorrectRequest() {
        List<ItemRequest> itemRequests = repository.getItemRequestByOwner(user1.getId());
        Assertions.assertEquals(1, itemRequests.size());
        Assertions.assertEquals(itemRequest1.getId(), itemRequests.get(0).getId());
    }

    @Test
    public void getItemRequestById_shouldReturnCorrectRequest() {
        Optional<ItemRequest> itemRequest = repository.getItemRequest(itemRequest1.getId());
        Assertions.assertEquals(itemRequest1.getId(), itemRequest.get().getId());
        Assertions.assertEquals(itemRequest1.getDescription(), itemRequest.get().getDescription());
        Assertions.assertEquals(itemRequest1.getCreated(), itemRequest.get().getCreated());
        Assertions.assertNotNull(itemRequest.get().getItems());
    }

    @Test
    public void getItemRequestsWithPagination_shouldReturnCorrectRequests() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").descending());
        Page<ItemRequest> itemRequests = repository.getItemRequestsWithPagination(itemRequest1.getOwner().getId(), pageable);
        Assertions.assertEquals(itemRequests.toList().size(), 2);
    }
}
