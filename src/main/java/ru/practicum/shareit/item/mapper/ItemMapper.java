package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDto mapToItemDto(Item item);

    Item mapToItem(ItemAddDto itemDto);

    Item mapToItem(ItemUpdateDto itemDto);
}