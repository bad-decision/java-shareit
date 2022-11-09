package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long ownerId);

    ItemDto getItem(Long itemId, Long userId);

    Item addItem(Long ownerId, Item item);

    Item updateItem(Long ownerId, Item item);

    void deleteItem(Long itemId);

    List<Item> searchItems(String text, boolean isAvailable);

    Comment addComment(CommentAddDto commentAddDto);
}
