package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getItemRequest(Long userId, Long itemRequestId);

    List<ItemRequestDto> getItemRequests(Long requestOwnerId);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestDto addItemRequest(ItemRequestAddDto dto);
}
