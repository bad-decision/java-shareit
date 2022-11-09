package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapper {
    BookingDto mapToBookingDto(Booking booking);

    @Mapping(target = "status", constant = "WAITING")
    Booking mapToBooking(BookingAddDto itemDto);

    @Mapping(target = "bookerId", source = "booker.id")
    ItemBookingDto mapToItemBookingDto(Booking booking);
}
