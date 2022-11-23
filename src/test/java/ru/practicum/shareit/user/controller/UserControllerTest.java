package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.exception.ConflictWithExistException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, UserMapper.class})
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private final UserAddDto userAddDto = new UserAddDto("test","test@mail.com");
    private final UserUpdateDto userUpdateDto = new UserUpdateDto("update", "update@mail.ru");
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void prepare() {
        user = new User();
        user.setId(1L);
        user.setName(userAddDto.getName());
        user.setEmail(userAddDto.getEmail());
        user.setItems(new ArrayList<>());
        user.setBookings(new ArrayList<>());
        user.setComments(new ArrayList<>());

        userDto = new UserDto(user.getId(),user.getName(),user.getEmail());
    }

    @Test
    public void getUser_shouldReturnUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @Test
    public void getUsers_shouldReturnUsers() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(user));

        mvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class));
    }

    @Test
    public void addUser_shouldReturnAddedUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userAddDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void addUser_shouldThrowException() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(user);

        userAddDto.setEmail(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userAddDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_shouldReturnUpdatedUser() throws Exception {
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        userDto.setName(userUpdateDto.getName());
        userDto.setEmail(userUpdateDto.getEmail());

        when(userService.updateUser(any()))
                .thenReturn(user);

        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void deleteUser_shouldReturnOk() throws Exception {
        mvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_shouldThrowException() throws Exception {
        when(userService.getUser(any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUser_shouldThrowConflictWithExistException() throws Exception {
        when(userService.getUser(any()))
                .thenThrow(ConflictWithExistException.class);

        mvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isConflict());
    }
}
