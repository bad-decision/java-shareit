package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Request to get users list");
        return userService.getUsers()
                .stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Request to get user: " + userId);
        return userMapper.mapToUserDto(userService.getUser(userId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserAddDto userAddDto) {
        User user = userMapper.mapToUser(userAddDto);
        log.info("Request to add new user: " + user);
        user = userService.addUser(user);
        return userMapper.mapToUserDto(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @Valid @RequestBody UserUpdateDto userUpdateDto) {
        User user = userMapper.mapToUser(userUpdateDto);
        user.setId(userId);
        log.info("Request to update user: " + user);
        user = userService.updateUser(user);
        return userMapper.mapToUserDto(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("Request to delete user: " + userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }
}
