package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.common.model.Entity;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User extends Entity<Long> {
    private Long id;
    private String name;
    private String email;
}
