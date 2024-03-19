package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingDTOShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.AccessException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final LocalDateTime now = LocalDateTime.now();

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .build();
    private final ItemDTO itemDTO = ItemDTO.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final List<Comment> comments = List.of(Comment.builder()
            .id(1L)
            .text("Text")
            .author(user)
            .build());
    private final List<Booking> bookings = List.of(Booking.builder()
            .id(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .status(BookingStatus.APPROVED)
            .item(item)
            .build());

    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository, itemMapper, bookingMapper, commentMapper);
    }

    @Test
    void addItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        assertEquals(itemDTO, itemService.addItem(1L, itemDTO));
    }

    @Test
    void addItemWithoutUserTest() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDTO));
    }

    @Test
    void addItemBlankNameTest() {
        ItemDTO badItemDTO = ItemDTO.builder()
                .name("")
                .description("description")
                .available(true)
                .build();
        assertThrows(ValidationException.class, () -> itemService.addItem(1L, badItemDTO));
    }

    @Test
    void addItemWithBadRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        itemDTO.setRequestId(1L);
        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDTO));
    }

    @Test
    void updateItemTest() {
        ItemDTO newItemDTO = ItemDTO.builder()
                .id(1L)
                .name("newItem")
                .description("newDescription")
                .available(false)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        assertEquals(newItemDTO, itemService.updateItem(1L, newItemDTO, 1));
    }

    @Test
    void updateItemNotExistTest() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDTO, 1));
    }

    @Test
    void updateItemNotOwnerExistTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(AccessException.class, () -> itemService.updateItem(2L, itemDTO, 1));
    }

    @Test
    void updateItemUseNotExistTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDTO, 1));
    }

    @Test
    void getItemByOwnerTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        lenient().when(commentRepository.findByItemIdIn(List.of(anyLong()))).thenReturn(comments);
        lenient().when(bookingRepository
                .findByItemIdInAndStatusNot(List.of(anyLong()), eq(BookingStatus.REJECTED))).thenReturn(bookings);
        itemDTO.setComments(new ArrayList<>());
        assertEquals(itemDTO, itemService.getItem(1L, 1L));
    }

    @Test
    void getItemUserNotExist() {
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void getAllItems() {
        Comment comment = Comment.builder()
                .id(1L)
                .author(user)
                .build();
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn((List.of(item)));
        lenient().when(bookingRepository
                .findByItemIdInAndStatusNot(List.of(anyLong()), eq(BookingStatus.REJECTED))).thenReturn(bookings);
        lenient().when(commentRepository.findByItemIdIn(List.of(anyLong()))).thenReturn(List.of(comment));
        assertEquals(List.of(itemDTO), itemService.getAllItems(1L, pageRequest));
    }

    @Test
    void findItemsByText() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(), anyString(),
                any(PageRequest.class))).thenReturn(List.of(item));
        assertEquals(List.of(itemDTO), itemService.findItems("text", pageRequest));
    }

    @Test
    void addCommentTest() {
        Comment comment = comments.get(0);
        Booking booking = bookings.get(0);
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@booker.com")
                .build();
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .text("Text")
                .authorName("booker")
                .build();
        when(bookingRepository.findFirstByItemIdAndBookerIdOrderByStart(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        booking.setStart(now.minusDays(1));
        booking.setBooker(booker);
        comment.setAuthor(booker);
        comment.setItem(item);
        lenient().when(commentRepository.save(any())).thenReturn(comment);
        assertEquals(commentDTO, itemService.addComment(2L, 1L, commentDTO));
    }

    @Test
    void addCommentBadTimeTest() {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .text("Text")
                .authorName("booker")
                .build();
        Booking booking = bookings.get(0);
        when(bookingRepository.findFirstByItemIdAndBookerIdOrderByStart(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(ValidationException.class, () -> itemService.addComment(2L, 1L, commentDTO));
    }
}