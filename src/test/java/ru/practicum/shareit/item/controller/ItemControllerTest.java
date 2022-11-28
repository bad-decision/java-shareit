package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentAddDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final ItemAddDto itemAddDto = new ItemAddDto("test", "test2", true, null, null);
    private final ItemUpdateDto itemUpdateDto = new ItemUpdateDto("test3", "test3", true);
    private final CommentAddDto commentAddDto = new CommentAddDto("test", 1L, 1L);
    private final CommentDto commentDto = new CommentDto(1L, commentAddDto.getText(), "test name", LocalDateTime.now());
    private Item item;
    private ItemDto dto;
    private Comment comment;

    @BeforeEach
    public void prepare() {
        item = new Item();
        item.setId(1L);
        item.setDescription(itemAddDto.getDescription());
        item.setName(itemAddDto.getName());
        item.setAvailable(itemAddDto.getAvailable());

        dto = new ItemDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(new ItemBookingDto(1L, 1L));

        comment = new Comment();
        comment.setId(1L);
        comment.setText(commentAddDto.getText());
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    public void getItem_shouldReturnItem() throws Exception {
        when(service.getItem(any(), any()))
                .thenReturn(dto);

        mvc.perform(get("/items/" + item.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class));
    }

    @Test
    public void getItems_shouldReturnItems() throws Exception {
        when(service.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class));
    }

    @Test
    public void getItems_shouldThrowException() throws Exception {
        when(service.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items?size=0")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItems_shouldReturnItems() throws Exception {
        when(service.searchItems(anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search?text=test")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class));
    }

    @Test
    public void searchItems_shouldThrowException() throws Exception {
        when(service.searchItems(anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search?size=0")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addItem_shouldReturnAddedItem() throws Exception {
        when(service.addItem(any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemAddDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }

    @Test
    public void updateItem_shouldReturnUpdatedItem() throws Exception {
        when(service.updateItem(anyLong(), any(Item.class)))
                .thenReturn(item);

        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    public void deleteItem_shouldReturnOk() throws Exception {
        mvc.perform(delete("/items/" + item.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void addComment_shouldReturnAddedComment() throws Exception {
        when(service.addComment(any()))
                .thenReturn(comment);

        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentAddDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}
