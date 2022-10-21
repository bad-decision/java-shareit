package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemDto mapToItemDto(Item item);
    @Mapping(target = "available", source = "available")
    Item mapToItem(ItemAddDto itemDto);
    @Mapping(target = "available", source = "available")
    Item mapToItem(ItemUpdateDto itemDto);
}
