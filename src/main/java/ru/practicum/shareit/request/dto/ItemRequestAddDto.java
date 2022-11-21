package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
public class ItemRequestAddDto {
    private Long itemRequestOwnerId;
    @NotBlank
    private String description;
}
