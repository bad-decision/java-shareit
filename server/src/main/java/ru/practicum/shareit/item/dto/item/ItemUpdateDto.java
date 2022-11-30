package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemUpdateDto {
    private String name;
    private String description;
    private Boolean available;
}
