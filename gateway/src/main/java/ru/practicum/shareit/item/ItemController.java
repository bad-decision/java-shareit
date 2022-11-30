package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get user items by ownerId {}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items by text {}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemAddDto itemAddDto) {
        log.info("Add new item {}, userId={}", itemAddDto, userId);
        return itemClient.addItem(userId, itemAddDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Update item {}, itemId={}, userId={}", itemUpdateDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        log.info("Delete item {}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody CommentAddDto commentAddDto) {
        log.info("Add comment {}, userId={}, itemId={}", commentAddDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentAddDto);
    }
}
