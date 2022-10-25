package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.repository.InMemoryRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRepositoryMemoryImpl extends InMemoryRepository<Item> implements ItemRepository {
    @Override
    public List<Item> getItemsBy(Long ownerId) {
        return objects.values()
                .stream()
                .filter(x -> x.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsBy(String text, boolean isAvailable) {
        String textLowerCase = text.toLowerCase();
        return objects.values()
                .stream()
                .filter(x -> x.getAvailable() == isAvailable &&
                        (x.getName().toLowerCase().contains(textLowerCase) ||
                                x.getDescription().toLowerCase().contains(textLowerCase)))
                .collect(Collectors.toList());
    }
}
