package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Request to get user items by userId: " + ownerId);
        return itemService.getItems(ownerId)
                .stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Request to get item: " + itemId);
        return itemMapper.mapToItemDto(itemService.getItem(itemId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Request to search items by text: " + text);
        return itemService.searchItems(text, true)
                .stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemAddDto itemAddDto,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = itemMapper.mapToItem(itemAddDto);
        log.info("Request to add new item: " + item);
        item = itemService.addItem(ownerId, item);
        return itemMapper.mapToItemDto(item);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        Item item = itemMapper.mapToItem(itemUpdateDto);
        item.setId(itemId);
        log.info("Request to update item: " + item);
        item = itemService.updateItem(ownerId, item);
        return itemMapper.mapToItemDto(item);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        log.info("Request to delete item: " + itemId);
        itemService.deleteItem(itemId);
        return new ResponseEntity<>(itemId, HttpStatus.OK);
    }
}
