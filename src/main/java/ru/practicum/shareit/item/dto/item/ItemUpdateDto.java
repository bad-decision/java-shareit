package ru.practicum.shareit.item.dto.item;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemUpdateDto {
    private String name;
    private String description;
    private Boolean available;
}
