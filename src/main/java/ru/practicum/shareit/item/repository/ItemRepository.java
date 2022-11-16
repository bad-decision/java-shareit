package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.author WHERE i.id=:itemId")
    Optional<Item> findByIdWithComments(Long itemId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.bookings LEFT JOIN FETCH i.comments WHERE i.owner.id=?1")
    Set<Item> getOwnerItemsWithBookingsAndComments(Long ownerId);

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%',LOWER(:text),'%') " +
            "OR LOWER(i.description) LIKE CONCAT('%',LOWER(:text),'%')) " +
            "AND i.available=:isAvailable")
    List<Item> searchItemsBy(String text, boolean isAvailable);
}
