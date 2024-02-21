package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private long countId = 1;
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item updateItem) {
        Item item = items.get(updateItem.getId());
        if (updateItem.getName() != null && !updateItem.getName().equals(item.getName())) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().equals(item.getDescription())) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null)
            item.setAvailable(updateItem.getAvailable());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems(long id) {
        return items.values().stream()
                .filter(i -> i.getOwner() == id)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    public void isItemExist(long id) {
        if (!items.containsKey(id)) throw new NotFoundException("Вещи с ID " + id + " не существует");
    }

    private long getId() {
        return countId++;
    }
}
