package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@DataJpaTest
@Transactional
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Item item1;

    @BeforeEach
    public void prepare() {
        User user1 = new User();
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

        item1 = new Item();
        item1.setAvailable(true);
        item1.setDescription("test1 desc");
        item1.setName("test1");
        item1.setOwner(user1);

        Item item2 = new Item();
        item2.setAvailable(true);
        item2.setDescription("test2 desc");
        item2.setName("test2");
        item2.setOwner(user2);

        Item item3 = new Item();
        item3.setAvailable(true);
        item3.setDescription("test3 desc");
        item3.setName("test3");
        item3.setOwner(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    public void findByIdWithComments_shouldReturnItems() {
        Optional<Item> item = itemRepository.findByIdWithComments(item1.getId());
        Assertions.assertEquals(item1.getId(), item.get().getId());
        Assertions.assertEquals(item1.getName(), item.get().getName());
        Assertions.assertNotNull(item.get().getComments());
    }

    @Test
    public void searchItemsBy_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Item> items = itemRepository.searchItemsBy("est", true, pageable);
        Assertions.assertEquals(3, items.toList().size());
    }

    @Test
    public void getOwnerItemsWithBookingsAndComments_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Item> items = itemRepository.getOwnerItemsWithBookingsAndComments(item1.getOwner().getId(), pageable);
        Assertions.assertEquals(1, items.toList().size());
    }
}
