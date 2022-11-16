package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private final CommentMapper commentMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Request to get user items by ownerId: " + ownerId);
        return itemService.getItems(ownerId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Request to get item: " + itemId);
        return itemService.getItem(itemId, userId);
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
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @Valid @RequestBody ItemAddDto itemAddDto) {
        Item item = itemMapper.mapToItem(itemAddDto);
        log.info("Request to add new item: " + item);
        item = itemService.addItem(ownerId, item);
        return itemMapper.mapToItemDto(item);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable("itemId") Long itemId,
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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                 @PathVariable("itemId") Long itemId,
                                 @Valid @RequestBody CommentAddDto commentAddDto) {
        commentAddDto.setItemId(itemId);
        commentAddDto.setAuthorId(authorId);
        log.info("Request to add comment commentAddDto: " + commentAddDto);
        Comment comment = itemService.addComment(commentAddDto);
        return commentMapper.mapToCommentDto(comment);
    }
}