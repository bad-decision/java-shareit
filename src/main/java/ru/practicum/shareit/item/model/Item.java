package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.common.model.Entity;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"owner"})
public class Item extends Entity<Long> {
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
