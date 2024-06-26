package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDTOShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.AccessException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    public static final String ITEM_NOT_FOUND = "Вещи с ID%d не существует!";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDTO addItem(Long userId, ItemDTO itemDTO) {
        if (itemDTO.getName() == null || itemDTO.getName().isBlank() ||
                itemDTO.getDescription() == null || itemDTO.getDescription().isBlank() ||
                itemDTO.getAvailable() == null) {
            throw new ValidationException("Не указано имя, описание или статус.");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Item item = itemMapper.toItem(itemDTO);
        item.setOwner(user);
        if (itemDTO.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDTO.getRequestId()).orElseThrow(() ->
                    new NotFoundException(String.format("Запрос с ID%d не найден", itemDTO.getRequestId())));
            item.setRequest(itemRequest);
        }
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long userId, ItemDTO itemDTO, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        isOwner(userId, item.getOwner().getId());
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        if (itemDTO.getName() != null && !itemDTO.getName().equals(item.getName())) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null && !itemDTO.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null && !itemDTO.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDTO.getAvailable());
        }
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDTO getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        ItemDTO itemDTO = itemMapper.toItemDTO(item);
        itemDTO.setComments(commentMapper.toCommentDTO(commentRepository.findByItemIdIn(List.of(itemId))));
        if (!userId.equals(item.getOwner().getId())) return itemDTO;
        List<Booking> bookings = bookingRepository
                .findByItemIdInAndStatusNot(List.of(itemId), BookingStatus.REJECTED);
        itemDTO.setLastBooking(findLastBooking(bookings));
        itemDTO.setNextBooking(findNextBooking(bookings));
        return itemDTO;
    }

    @Override
    @Transactional
    public List<ItemDTO> getAllItems(Long userId, PageRequest pageRequest) {
        List<ItemDTO> items = itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
        List<Long> itemIds = items.stream()
                .map(ItemDTO::getId)
                .collect(Collectors.toList());
        Map<Long, List<Booking>> bookingsByItemId = bookingRepository
                .findByItemIdInAndStatusNot(itemIds, BookingStatus.REJECTED).stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentsMapByItemId = commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        return items.stream()
                .peek(i -> i.setLastBooking(findLastBooking(bookingsByItemId.get(i.getId()))))
                .peek(i -> i.setNextBooking(findNextBooking(bookingsByItemId.get(i.getId()))))
                .peek(i -> i.setComments(commentMapper.toCommentDTO(commentsMapByItemId.get(i.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDTO> findItems(String text, PageRequest pageRequest) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        List<Item> list = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                pageRequest);
        return list.stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO addComment(Long bookerId, Long itemId, CommentDTO commentDTO) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdOrderByStart(itemId, bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID%d не бронировал вещь с ID%d ",
                        bookerId, itemId)));
        if (booking.getStart().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Пользователь может оставлять отзыв только после начала броинрования!");
        }
        Comment comment = commentMapper.toComment(commentDTO);
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDTO(commentRepository.save(comment));
    }

    private BookingDTOShort findLastBooking(List<Booking> bookingList) {
        if (bookingList == null) return null;
        return bookingList.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .map(bookingMapper::toBookingDTOShort).orElse(null);
    }

    private BookingDTOShort findNextBooking(List<Booking> bookingList) {
        if (bookingList == null) return null;
        return bookingList.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDTOShort).orElse(null);
    }

    @Override
    public void isOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId))
            throw new AccessException(String.format("Пользователь ID%d не имеет доступа к вещи ID%d.",
                    userId, ownerId));
    }
}