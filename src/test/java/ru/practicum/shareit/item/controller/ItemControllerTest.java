package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class, ItemMapper.class, CommentMapper.class})
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;

    private final ItemAddDto addDto = new ItemAddDto("test", "test2", true, null, null);

    @Test
    public void addItem_shouldReturnAddedItem() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setDescription(addDto.getDescription());
        item.setName(addDto.getName());
        item.setAvailable(addDto.getAvailable());

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());
        dto.setAvailable(item.getAvailable());

        when(service.addItem(any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(addDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }
}
