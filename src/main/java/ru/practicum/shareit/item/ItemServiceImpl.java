package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.AccessException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDTO addItem(Long userId, ItemDTO itemDto) {
        userRepository.isUserExist(userId);
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new ValidationException("Не указано имя, описание или статус.");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDTO updateItem(Long userId, ItemDTO itemDto, long itemId) {
        userRepository.isUserExist(userId);
        itemRepository.isItemExist(itemId);
        itemDto.setId(itemId);
        isOwner(userId, itemRepository.getItem(itemId).getOwner());
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDTO getItem(long id) {
        itemRepository.isItemExist(id);
        return ItemMapper.toItemDto(itemRepository.getItem(id));
    }

    @Override
    public List<ItemDTO> getAllItems(long id) {
        return itemRepository.getAllItems(id).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        List<Item> list = itemRepository.findItems(text.toLowerCase());
        return list.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public void isOwner(long userId, long itemId) {
        if (userId != itemId)
            throw new AccessException(String.format("Пользователь ID%d не имеет доступа к вещи ID%d.",
                    userId, itemId));
    }
}
