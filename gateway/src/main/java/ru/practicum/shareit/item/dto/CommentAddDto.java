package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentAddDto {
    @NotBlank
    private String text;
}
