package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateDto {
    private String name;
    @Email
    private String email;
}
