package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDTO;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final User user = new User(1L, "user@email.com", "User");
    private final UserDTO userDTO = new UserDTO(1L, "user@email.com", "User");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requestor(user)
            .description("description")
            .build();
    private final ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
            .id(1L)
            .description("description")
            .requestor(userDTO)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .build();
    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void init() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository,
                itemRequestMapper, itemMapper);
    }

    @Test
    void addItemRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        itemRequestDTO.setItems(null);
        Assertions.assertEquals(itemRequestDTO, itemRequestService.addItemRequest(1L, itemRequestDTO));
    }

    @Test
    void addItemRequestUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(1L, itemRequestDTO));
    }

    @Test
    void getOwnerItemRequestsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        lenient().when(itemRepository.findByRequestIdIn(List.of(anyLong()))).thenReturn(List.of(item));
        assertEquals(List.of(itemRequestDTO), itemRequestService.getOwnerItemRequests(1L));
    }

    @Test
    void getOwnerItemRequestsUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getOwnerItemRequests(1L));
    }

    @Test
    void getItemRequestsTest() {
        when(itemRequestRepository
                .findByRequestorIdIsNot(anyLong(), any(PageRequest.class))).thenReturn(
                new PageImpl<>(List.of(itemRequest), pageRequest, 10));
        lenient().when(itemRepository.findByRequestIdIn(List.of(anyLong()))).thenReturn(List.of(item));
        assertEquals(List.of(itemRequestDTO), itemRequestService.getItemRequests(1L, pageRequest));
    }

    @Test
    void getRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        lenient().when(itemRepository.findByRequestIdIn(List.of(anyLong()))).thenReturn(List.of(item));
        assertEquals(itemRequestDTO, itemRequestService.getItemRequest(1L, 1L));
    }

    @Test
    void getRequestUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(1L, 1L));
    }
    @Test
    void getRequestRequestNotExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(1L, 1L));
    }
}