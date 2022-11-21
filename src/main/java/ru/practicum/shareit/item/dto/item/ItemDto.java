package ru.practicum.shareit.item.dto.item;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private Set<CommentDto> comments;
    private Long requestId;
}
