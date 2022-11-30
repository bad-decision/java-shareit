package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items " +
            "WHERE ir.owner.id=:requestOwnerId ORDER BY ir.created DESC")
    List<ItemRequest> getItemRequestByOwner(Long requestOwnerId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.id=:id")
    Optional<ItemRequest> getItemRequest(Long id);

    @Query("SELECT DISTINCT ir FROM ItemRequest ir LEFT JOIN ir.items WHERE ir.owner.id<>:userId")
    Page<ItemRequest> getItemRequestsWithPagination(Long userId, Pageable pageable);
}
