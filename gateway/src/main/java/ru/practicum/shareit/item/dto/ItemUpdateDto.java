package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemUpdateDto {
    private String name;
    private String description;
    private Boolean available;
}
