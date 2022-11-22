package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item1;
    private Booking booking1;

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

        booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setBooker(user3);
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.WAITING);

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking3.setEnd(LocalDateTime.now().plusDays(2));
        booking3.setBooker(user1);
        booking3.setItem(item3);
        booking3.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @Test
    public void getIntersectedBookings_shouldReturnIntersectedBooking() {
        List<Booking> bookings = bookingRepository.getIntersectedBookings(item1.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    public void getBookingsByBookerAndItem_shouldReturnBookings() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerAndItem(booking1.getBooker().getId(), booking1.getItem().getId());
        Assertions.assertEquals(1, bookings.size());
    }
}
