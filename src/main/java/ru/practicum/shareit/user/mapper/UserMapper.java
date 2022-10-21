package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {
    UserDto mapToUserDto(User user);
    User mapToUser(UserAddDto userDto);
    User mapToUser(UserUpdateDto userDto);
}
