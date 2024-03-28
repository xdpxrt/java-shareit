package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequestDTO;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDTO addItemRequest(Long userId, ItemRequestDTO itemRequestDTO);

    List<ItemRequestDTO> getOwnerItemRequests(Long ownerId);

    List<ItemRequestDTO> getItemRequests(Long userId, PageRequest pageRequest);

    ItemRequestDTO getItemRequest(Long userId, Long requestId);
}