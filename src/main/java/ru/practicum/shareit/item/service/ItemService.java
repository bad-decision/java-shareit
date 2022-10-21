package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long ownerId);
    Item getItem(Long itemId);
    Item addItem(Long ownerId, Item item);
    Item updateItem(Long ownerId, Item item);
    void deleteItem(Long itemId);
    List<Item> searchItems(String text, boolean isAvailable);
}
