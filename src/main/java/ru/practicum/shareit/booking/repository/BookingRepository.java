package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.item.id=:itemId AND " +
            "(b.start BETWEEN :startDate AND :endDate OR b.end BETWEEN :startDate AND :endDate)")
    List<Booking> getIntersectedBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.item.id=?2")
    List<Booking> getBookingsByBookerAndItem(Long bookerId, Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1")
    Page<Booking> getBookingsByBookerAndStatus(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.status=?2")
    Page<Booking> getBookingsByBookerAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.start>?2")
    Page<Booking> getBookingsByBookerAndStartAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.end<?2")
    Page<Booking> getBookingsByBookerAndEndBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.start<=?2 AND b.end>=?2")
    Page<Booking> getBookingsByBookerAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1")
    Page<Booking> getBookingsByOwnerAndStatus(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.status=?2")
    Page<Booking> getBookingsByOwnerAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.start>?2")
    Page<Booking> getBookingsByOwnerAndStartAfter(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.end<?2")
    Page<Booking> getBookingsByOwnerAndEndBefore(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.start<=?2 AND b.end>=?2")
    Page<Booking> getBookingsByOwnerAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime date, Pageable pageable);
}
