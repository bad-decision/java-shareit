package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserAddDto {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
