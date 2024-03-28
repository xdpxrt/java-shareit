package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDTO;

import java.util.List;

@Mapper(componentModel = "spring")

public interface ItemRequestMapper {

    ItemRequestDTO toItemRequestDTO(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO);

    List<ItemRequestDTO> toItemRequestDTO(List<ItemRequest> itemRequest);
}