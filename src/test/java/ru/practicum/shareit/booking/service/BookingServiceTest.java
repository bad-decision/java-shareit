package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class BookingServiceTest {
    private BookingService bookingService;
    private Item item;
    private User booker;
    private Booking booking;
    private BookingAddDto bookingAddDto;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Autowired
    private BookingMapper bookingMapper;

    @BeforeEach
    public void prepare() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository, bookingMapper);
        booking = createDummyBooking();
        booker = createDummyUser(2L);
        item = createDummyItem();
        bookingAddDto = createDummyBookingAddDto();
    }

    @Test
    public void addBookingWithWrongDates_shouldThrowException() {
        bookingAddDto.setEnd(bookingAddDto.getStart().minusDays(1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingAddDto));
    }

    @Test
    public void addBookingWithNotExistBooker_shouldThrowException() {
        Mockito
                .when(userRepository.findById(bookingAddDto.getBookerId() + 1))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingAddDto));
    }

    @Test
    public void addBookingWithNotExistItem_shouldThrowException() {
        Mockito
                .when(userRepository.findById(bookingAddDto.getBookerId()))
                .thenReturn(Optional.ofNullable(booking.getBooker()));
        Mockito
                .when(itemRepository.findByIdWithComments(bookingAddDto.getItemId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingAddDto));
    }

    @Test
    public void addBookingNotAvailableItem_shouldThrowException() {
        booking.getItem().setAvailable(false);
        Mockito
                .when(userRepository.findById(bookingAddDto.getBookerId()))
                .thenReturn(Optional.ofNullable(booking.getBooker()));
        Mockito
                .when(itemRepository.findByIdWithComments(bookingAddDto.getItemId()))
                .thenReturn(Optional.ofNullable(booking.getItem()));
        Assertions.assertThrows(PermissionException.class, () -> bookingService.addBooking(bookingAddDto));
    }

    @Test
    public void addBookingByOwner_shouldThrowException() {
        Mockito
                .when(userRepository.findById(bookingAddDto.getBookerId()))
                .thenReturn(Optional.ofNullable(booking.getBooker()));
        Mockito
                .when(itemRepository.findByIdWithComments(bookingAddDto.getItemId()))
                .thenReturn(Optional.ofNullable(booking.getItem()));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingAddDto));
    }

    @Test
    public void addBooking_shouldReturnAddedBooking() {
        bookingAddDto.setBookerId(booker.getId());
        Mockito
                .when(userRepository.findById(bookingAddDto.getBookerId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findByIdWithComments(bookingAddDto.getItemId()))
                .thenReturn(Optional.ofNullable(booking.getItem()));
        Booking addedBooking = bookingService.addBooking(bookingAddDto);
        Assertions.assertEquals(bookingAddDto.getEnd(), addedBooking.getEnd());
        Assertions.assertEquals(bookingAddDto.getStart(), addedBooking.getStart());
        Assertions.assertEquals(bookingAddDto.getItemId(), addedBooking.getItem().getId());
        Assertions.assertEquals(bookingAddDto.getBookerId(), addedBooking.getBooker().getId());
    }

    @Test
    public void approveBooking_shouldReturnBooking() {
        Mockito
                .when(userRepository.findById(booking.getItem().getOwner().getId()))
                .thenReturn(Optional.ofNullable(booking.getItem().getOwner()));
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));
        Booking approvedBooking = bookingService.approveBooking(booking.getId(), booking.getItem().getOwner().getId(), true);
        Assertions.assertEquals(true, approvedBooking.getItem().getAvailable());
    }

    @Test
    public void getBooking_shouldReturnBooking() {
        Mockito
                .when(userRepository.findById(booking.getBooker().getId()))
                .thenReturn(Optional.ofNullable(booking.getBooker()));
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));
        Booking foundBooking = bookingService.getBooking(booking.getId(), booking.getBooker().getId());
        Assertions.assertEquals(booking.getId(), foundBooking.getId());
        Assertions.assertEquals(booking.getStatus(), foundBooking.getStatus());
        Assertions.assertEquals(booking.getStart(), foundBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), foundBooking.getEnd());
    }

    @Test
    public void getOwnerBookings_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(1, 1, Sort.by("start").descending());
        Mockito
                .when(userRepository.findById(booking.getItem().getOwner().getId()))
                .thenReturn(Optional.ofNullable(booking.getItem().getOwner()));
        Mockito
                .when(bookingRepository.getBookingsByOwnerAndStatus(booking.getItem().getOwner().getId(), BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<Booking> bookings = bookingService.getOwnerBookings(booking.getItem().getOwner().getId(), "WAITING", 1, 1);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    public void getBookerBookings_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(1, 1, Sort.by("start").descending());
        Mockito
                .when(userRepository.findById(booking.getBooker().getId()))
                .thenReturn(Optional.ofNullable(booking.getBooker()));
        Mockito
                .when(bookingRepository.getBookingsByBookerAndStatus(booking.getBooker().getId(), BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<Booking> bookings = bookingService.getBookerBookings(booking.getBooker().getId(), "WAITING", 1, 1);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    private Booking createDummyBooking() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setId(1L);
        booking.setBooker(createDummyUser(1L));
        booking.setItem(createDummyItem());
        return booking;
    }

    private Item createDummyItem() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(createDummyUser(1L));
        item.setName("test item");
        item.setAvailable(true);
        item.setDescription("test description");
        item.setItemRequest(createDummyRequest());
        return item;
    }

    private ItemRequest createDummyRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test request");
        return itemRequest;
    }

    private User createDummyUser(Long id) {
        User owner = new User();
        owner.setId(id);
        owner.setEmail("owner@mal.ru");
        owner.setName("owner");
        return owner;
    }

    private BookingAddDto createDummyBookingAddDto() {
        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setItemId(item.getId());
        bookingAddDto.setBookerId(1L);
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        return bookingAddDto;
    }
}