package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Request to get booking: " + bookingId);
        return bookingMapper.mapToBookingDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                              @RequestParam(defaultValue = "ALL") BookingStateDto state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get booker bookings: " + bookerId + ", state: " + state);
        return bookingService.getBookerBookings(bookerId, state, from, size)
                .stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(defaultValue = "ALL") BookingStateDto state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get owner bookings: " + ownerId + ", state: " + state);
        return bookingService.getOwnerBookings(ownerId, state, from, size)
                .stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                 @Valid @RequestBody BookingAddDto bookingAddDto) {
        bookingAddDto.setBookerId(bookerId);
        log.info("Request to add new booking: " + bookingAddDto);
        Booking booking = bookingService.addBooking(bookingAddDto);
        return bookingMapper.mapToBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        log.info("Request to add approve booking: " + bookingId + ", ownerId: " + ownerId + ", approved: " + approved);
        Booking booking = bookingService.approveBooking(bookingId, ownerId, approved);
        return bookingMapper.mapToBookingDto(booking);
    }
}
