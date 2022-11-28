package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    private String description;
    private Set<ItemDto> items = new HashSet<>();

    @Getter
    @Setter
    public static class ItemDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}
