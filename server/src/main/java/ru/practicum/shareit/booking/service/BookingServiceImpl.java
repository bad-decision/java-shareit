package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ConflictWithExistException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookerBookings(Long bookerId, BookingStateDto state, int from, int size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.getBookingsByBookerAndStatus(bookerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.getBookingsByBookerAndEndBefore(bookerId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingsByBookerAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingsByBookerAndStartAfter(bookerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.getBookingsByBookerAndStatus(bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.getBookingsByBookerAndStatus(bookerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new IllegalArgumentException("Not supported state: " + state);
        }
        return bookings.stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getOwnerBookings(Long ownerId, BookingStateDto state, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.getBookingsByOwnerAndStatus(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.getBookingsByOwnerAndEndBefore(ownerId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingsByOwnerAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingsByOwnerAndStartAfter(ownerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.getBookingsByOwnerAndStatus(ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.getBookingsByOwnerAndStatus(ownerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new IllegalArgumentException("Not supported state: " + state);
        }
        return bookings.stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException("User is not an owner of item or booker. BookingId: " + bookingId + ", userId: " + userId);

        return booking;
    }

    @Override
    public Booking addBooking(BookingAddDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()))
            throw new IllegalArgumentException("Booking start date is after than end date for itemId: " + dto.getItemId());

        User booker = userRepository.findById(dto.getBookerId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + dto.getBookerId()));
        Item item = itemRepository.findByIdWithComments(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + dto.getItemId()));

        if (!item.getAvailable())
            throw new PermissionException("Item is not available itemId: " + dto.getItemId());

        if (Objects.equals(booker.getId(), item.getOwner().getId()))
            throw new NotFoundException("Owner cannot book own item");

        List<Booking> intersectedBookings = bookingRepository.getIntersectedBookings(dto.getItemId(), dto.getStart(), dto.getEnd());
        if (intersectedBookings.size() > 0)
            throw new ConflictWithExistException("This dates are already booked for itemId: " + dto.getItemId());

        Booking booking = bookingMapper.mapToBooking(dto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, boolean approved) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId))
            throw new NotFoundException("User is not an owner of item. OwnerId: " + ownerId + ", bookingId: " + bookingId);

        if (booking.getStatus() != BookingStatus.WAITING)
            throw new IllegalArgumentException("Bookings is not in waiting status, bookingId: " + bookingId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return booking;
    }
}
