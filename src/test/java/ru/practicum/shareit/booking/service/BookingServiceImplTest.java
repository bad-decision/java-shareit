package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    public void addBooking_shouldAddNewBooking() {
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

        Item item = itemService.addItem(itemAddDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setItemId(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setBookerId(booker.getId());

        Booking addedBooking = bookingService.addBooking(bookingAddDto);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", addedBooking.getId()).getSingleResult();

        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(bookingAddDto.getStart(), booking.getStart());
        Assertions.assertEquals(bookingAddDto.getEnd(), booking.getEnd());
    }
}