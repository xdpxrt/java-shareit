package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO addItem(Long userId, ItemDTO itemDto);

    ItemDTO updateItem(Long userId, ItemDTO itemDto, long itemId);

    ItemDTO getItem(Long userId, Long itemId);

    List<ItemDTO> getAllItems(Long id, PageRequest pageRequest);

    List<ItemDTO> findItems(String item, PageRequest pageRequest);

    void isOwner(Long userId, Long itemId);

    CommentDTO addComment(Long bookerId, Long itemId, CommentDTO commentDTO);
}
