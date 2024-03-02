package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDTOShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.AccessException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    public static final String ITEM_NOT_FOUND = "Вещи с ID%d не существует!";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDTO addItem(Long userId, ItemDTO itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new ValidationException("Не указано имя, описание или статус.");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    public ItemDTO updateItem(Long userId, ItemDTO itemDto, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        isOwner(userId, item.getOwner().getId());
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    public ItemDTO getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        if (!userId.equals(item.getOwner().getId())) return itemMapper.toItemDTO(item);
        List<Booking> bookings = bookingRepository
                .findByItemIdInAndStatusNot(List.of(itemId), BookingStatus.REJECTED);
        ItemDTO itemDTO = itemMapper.toItemDTO(item);
        itemDTO.setLastBooking(findLastBooking(itemId, bookings));
        itemDTO.setNextBooking(findNextBooking(itemId, bookings));
        return itemDTO;
    }

    @Override
    public List<ItemDTO> getAllItems(Long userId) {
        List<ItemDTO> items = itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
        List<Long> itemIds = items.stream()
                .map(ItemDTO::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(itemIds, BookingStatus.REJECTED);
        return items.stream()
                .peek(i -> i.setLastBooking(findLastBooking(i.getId(), bookings)))
                .peek(i -> i.setNextBooking(findNextBooking(i.getId(), bookings)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        List<Item> list = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        return list.stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void isOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId))
            throw new AccessException(String.format("Пользователь ID%d не имеет доступа к вещи ID%d.",
                    userId, ownerId));
    }

    private BookingDTOShort findLastBooking(Long itemId, List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> Objects.equals(b.getItem().getId(), itemId))
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDTOShort).orElse(null);
    }

    private BookingDTOShort findNextBooking(Long itemId, List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> Objects.equals(b.getItem().getId(), itemId))
                .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDTOShort).orElse(null);
    }
}