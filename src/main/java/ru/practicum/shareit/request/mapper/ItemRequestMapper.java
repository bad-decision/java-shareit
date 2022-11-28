package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemRequestDto.ItemDto mapToRequestItemDto(Item item);

    ItemRequest mapToItemRequest(ItemRequestAddDto itemRequestAddDto);
}