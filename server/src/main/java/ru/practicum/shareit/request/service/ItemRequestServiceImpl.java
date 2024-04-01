package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDTO;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDTO addItemRequest(Long userId, ItemRequestDTO itemRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDTO);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.toItemRequestDTO(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    @Override
    public List<ItemRequestDTO> getOwnerItemRequests(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, ownerId)));
        List<ItemRequestDTO> requests = itemRequestMapper.toItemRequestDTO(
                itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(ownerId));
        return !requests.isEmpty() ? fillItemRequestDTOWithItems(requests) : new ArrayList<>();
    }

    @Transactional
    @Override
    public List<ItemRequestDTO> getItemRequests(Long userId, PageRequest pageRequest) {
        Page<ItemRequest> page = itemRequestRepository
                .findByRequestorIdIsNot(userId, pageRequest);
        List<ItemRequestDTO> requests = itemRequestMapper.toItemRequestDTO(page.toList());
        return !requests.isEmpty() ? fillItemRequestDTOWithItems(requests) : new ArrayList<>();
    }

    @Transactional
    @Override
    public ItemRequestDTO getItemRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с ID%d не существует!", requestId)));
        return fillItemRequestDTOWithItems(List.of(itemRequestMapper.toItemRequestDTO(request))).get(0);
    }

    @Transactional
    private List<ItemRequestDTO> fillItemRequestDTOWithItems(List<ItemRequestDTO> requests) {
        List<Long> requestsIds = requests.stream()
                .map(ItemRequestDTO::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsMap = itemRepository.findByRequestIdIn(requestsIds).stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));
        return requests.stream()
                .peek(r -> r.setItems(itemMapper.toItemDTO(itemsMap.getOrDefault(r.getId(), new ArrayList<>()))))
                .collect(Collectors.toList());
    }
}