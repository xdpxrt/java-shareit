package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDTO addItem(Long userId, ItemDTO itemDto);

    ItemDTO updateItem(Long userId, ItemDTO itemDto, long itemId);

    ItemDTO getItem(long id);

    List<ItemDTO> getAllItems(long id);

    List<ItemDTO> findItems(String item);

    void isOwner(long userId, long itemId);
}
