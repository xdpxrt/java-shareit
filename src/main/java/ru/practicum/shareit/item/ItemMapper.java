package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemDTO itemDTO);

    ItemDTO toItemDTO(Item item);
}
