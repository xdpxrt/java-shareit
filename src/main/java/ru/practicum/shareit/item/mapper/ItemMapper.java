package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemDTO itemDTO);

    ItemDTO toItemDTO(Item item);
}
