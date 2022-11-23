package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, BookingMapper.class})
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;
    private Booking booking;
    private BookingDto bookingDto;

    private final BookingAddDto addDto = new BookingAddDto(1L, 1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

    @BeforeEach
    public void prepare() {
        User owner = new User();
        owner.setEmail("test@mail.ru");
        owner.setName("test");
        owner.setId(1L);

        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthor(owner);
        comment.setId(1L);

        Item item = new Item();
        item.setOwner(owner);
        item.setDescription("test desc");
        item.setId(1L);
        item.setName("test name");
        item.setAvailable(true);
        item.setComments(Set.of(comment));

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(addDto.getStart());
        booking.setEnd(addDto.getEnd());
        booking.setItem(item);
        booking.setBooker(owner);

        bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
    }

    @Test
    public void getBooking_shouldReturnBooking() throws Exception {
        when(service.getBooking(any(), any()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/" + booking.getId())
                                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    public void getBookerBookings_shouldReturnBookings() throws Exception {
        when(service.getBookerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }

    @Test
    public void getBookerBookings_shouldThrowException() throws Exception {
        when(service.getBookerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings?size=0")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOwnerBookings_shouldReturnBookings() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }

    @Test
    public void getOwnerBookings_shouldThrowException() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?size=0")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBooking_shouldReturnAddedBooking() throws Exception {
        when(service.addBooking(any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void approveBooking_shouldReturnApprovedBooking() throws Exception {
        when(service.approveBooking(1L, 1L, true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }
}
