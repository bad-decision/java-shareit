package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestAddDto itemRequestAddDto = new ItemRequestAddDto(1L,"test");
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void prepare() {
        Item item = new Item();
        item.setName("test");
        item.setDescription("test desc");
        item.setId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(itemRequestAddDto.getDescription());
        itemRequest.setItems(Set.of(item));

        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setRequestId(1L);
        itemDto.setName("test");
        itemDto.setAvailable(true);
        itemDto.setDescription("test desc");
        itemDto.setId(1L);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setItems(Set.of(itemDto));
    }

    @Test
    public void getItemRequest_shouldReturnItemRequest() throws Exception {
        when(service.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/" + itemRequest.getId())
                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class));
    }

    @Test
    public void getItemRequests_shouldReturnItemRequests() throws Exception {
        when(service.getItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class));
    }

    @Test
    public void getAllItemRequests_shouldReturnItemRequests() throws Exception {
        when(service.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class));
    }

    @Test
    public void getAllItemRequests_shouldReturnBadRequest() throws Exception {
        when(service.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/requests/all?size=0")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addRequest_shouldReturnAddedRequest() throws Exception {
        when(service.addItemRequest(any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestAddDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}
