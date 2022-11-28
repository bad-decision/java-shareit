package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.author WHERE i.id=:itemId")
    Optional<Item> findByIdWithComments(Long itemId);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN i.bookings LEFT JOIN i.comments WHERE i.owner.id=?1")
    Page<Item> getOwnerItemsWithBookingsAndComments(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%',LOWER(:text),'%') " +
            "OR LOWER(i.description) LIKE CONCAT('%',LOWER(:text),'%')) " +
            "AND i.available=:isAvailable")
    Page<Item> searchItemsBy(String text, boolean isAvailable, Pageable pageable);
}
