package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> getBookerBookings(Long bookerId, String state, int from, int size);

    List<Booking> getOwnerBookings(Long ownerId, String state, int from, int size);

    Booking getBooking(Long bookingId, Long userId);

    Booking addBooking(BookingAddDto bookingAddDto);

    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);
}
