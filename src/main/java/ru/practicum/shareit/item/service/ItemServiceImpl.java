package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getItems(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));
        return itemRepository.getItemsBy(ownerId);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    @Override
    public Item addItem(Long ownerId, Item item) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));
        item.setOwner(owner);
        return itemRepository.add(item);
    }

    @Override
    public Item updateItem(Long ownerId, Item item) {
        Item existItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + item.getId()));

        if (!Objects.equals(ownerId, existItem.getOwner().getId()))
            throw new NotFoundException("User is not a owner of item. OwnerId: " + ownerId + ", item: " + item);

        if (item.getName() != null)
            existItem.setName(item.getName());

        if (item.getDescription() != null)
            existItem.setDescription(item.getDescription());

        if (item.getAvailable() != null)
            existItem.setAvailable(item.getAvailable());

        itemRepository.update(existItem);
        return existItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<Item> searchItems(String text, boolean isAvailable) {
        if (text == null || text.isEmpty()) return List.of();

        return itemRepository.searchItemsBy(text, isAvailable);
    }
}
