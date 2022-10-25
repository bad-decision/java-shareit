package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.common.repository.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends Repository<Item, Long> {
    List<Item> getItemsBy(Long ownerId);

    List<Item> searchItemsBy(String text, boolean isAvailable);
}
