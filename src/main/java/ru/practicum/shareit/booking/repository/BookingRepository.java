package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.item.id=:itemId AND " +
            "(b.start BETWEEN :startDate AND :endDate OR b.end BETWEEN :startDate AND :endDate)")
    List<Booking> getIntersectedBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.item.id=?2")
    List<Booking> getBookingsByBookerAndItem(Long bookerId, Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1")
    List<Booking> getBookingsByBookerAndStatus(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.status=?2")
    List<Booking> getBookingsByBookerAndStatus(Long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.start>?2")
    List<Booking> getBookingsByBookerAndStartAfter(Long bookerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.end<?2")
    List<Booking> getBookingsByBookerAndEndBefore(Long bookerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=?1 AND b.start<=?2 AND b.end>=?2")
    List<Booking> getBookingsByBookerAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1")
    List<Booking> getBookingsByOwnerAndStatus(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.status=?2")
    List<Booking> getBookingsByOwnerAndStatus(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.start>?2")
    List<Booking> getBookingsByOwnerAndStartAfter(Long ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.end<?2")
    List<Booking> getBookingsByOwnerAndEndBefore(Long ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id=?1 AND b.start<=?2 AND b.end>=?2")
    List<Booking> getBookingsByOwnerAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime date);
}
