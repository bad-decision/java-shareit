package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentAddDto;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long ownerId, int from, int size);

    ItemDto getItem(Long itemId, Long userId);

    Item addItem(ItemAddDto dto);

    Item updateItem(Long ownerId, Item item);

    void deleteItem(Long itemId);

    List<Item> searchItems(String text, boolean isAvailable, int from, int size);

    Comment addComment(CommentAddDto commentAddDto);
}
