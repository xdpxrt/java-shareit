package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(long id);

    List<Item> getAllItems(long id);

    List<Item> findItems(String item);

    public void isItemExist(long id);
}
