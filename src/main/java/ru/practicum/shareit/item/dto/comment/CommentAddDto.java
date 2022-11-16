package ru.practicum.shareit.item.dto.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class CommentAddDto {
    @NotBlank
    private String text;
    private Long itemId;
    private Long authorId;
}
