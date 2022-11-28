package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestAddDto {
    private Long itemRequestOwnerId;
    @NotBlank
    private String description;
}
