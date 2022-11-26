package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long itemRequestOwnerId) {
        log.info("Request to get own item requests");
        return itemRequestService.getItemRequests(itemRequestOwnerId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get all item requests from: " + from + ", size: " + size);
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable(name = "requestId") Long itemRequestId) {
        log.info("Request to get item request with id: " + itemRequestId);
        return itemRequestService.getItemRequest(userId, itemRequestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long itemRequestOwnerId,
                                         @Valid @RequestBody ItemRequestAddDto itemRequestAddDto) {
        itemRequestAddDto.setItemRequestOwnerId(itemRequestOwnerId);
        log.info("Request to add new item request: " + itemRequestAddDto);
        return itemRequestService.addItemRequest(itemRequestAddDto);
    }
}
