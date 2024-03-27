package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.item.service.ItemServiceImpl.ITEM_NOT_FOUND;
import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final String BOOKING_NOT_FOUND = "Броинрования с ID%d не существует!";
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDTO add(Long userId, BookingDTOInput bookingDTOInput) {
        if (bookingDTOInput.getStart().isAfter(bookingDTOInput.getEnd()) ||
                bookingDTOInput.getStart().equals((bookingDTOInput.getEnd()))) {
            throw new BadRequestException(String.format("Время начала = %s или конца = %s бронирования неверное",
                    bookingDTOInput.getStart(), bookingDTOInput.getEnd()));
        }
        Item item = itemRepository.findById(bookingDTOInput.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, bookingDTOInput.getItemId())));
        if (!item.getAvailable()) throw new ValidationException("Вещь не доступна для бронирования.");
        if (userId.equals(item.getOwner().getId()))
            throw new NotFoundException("Владелец не может забронировать свою вещь.");
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Booking booking = bookingMapper.toBooking(bookingDTOInput, item, booker, BookingStatus.WAITING);
        return bookingMapper.toBookingDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO approve(Long userId, Long bookingId, boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId()))
            throw new NotFoundException("Изменить статус может только владелец!");
        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new BadRequestException("Статус бронирования уже 'APPROVED'");
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId)));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId()))
            throw new NotFoundException("Доступ к бронированию имеет только владелец или арендатор!");
        return bookingMapper.toBookingDTO(booking);
    }

    @Override
    @Transactional
    public List<BookingDTO> getAllByBooker(Long booker, BookingState bookingState, PageRequest pageRequest) {
        userExistence(booker);
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdOrderByStartDesc(booker, pageRequest).toList());
            case PAST:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(booker, now, pageRequest).toList());
            case FUTURE:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(booker, now, pageRequest).toList());
            case CURRENT:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker, now, now, pageRequest)
                        .toList());
            case WAITING:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(booker, BookingStatus.WAITING, pageRequest)
                        .toList());
            case REJECTED:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(booker, BookingStatus.REJECTED, pageRequest)
                        .toList());
            default:
                throw new ValidationException("Неверно указан статус");
        }
    }

    @Override
    @Transactional
    public List<BookingDTO> getAllByOwner(Long owner, BookingState bookingState, PageRequest pageRequest) {
        userExistence(owner);
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingMapper.toBookingDTO(
                        bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner, pageRequest).toList());
            case PAST:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner, now, pageRequest).toList());
            case FUTURE:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner, now, pageRequest).toList());
            case CURRENT:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner, now, now, pageRequest)
                        .toList());
            case WAITING:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByItemOwnerIdAndStatusIsOrderByStartDesc(owner, BookingStatus.WAITING, pageRequest)
                        .toList());
            case REJECTED:
                return bookingMapper.toBookingDTO(bookingRepository
                        .findAllByItemOwnerIdAndStatusIsOrderByStartDesc(owner, BookingStatus.REJECTED, pageRequest)
                        .toList());
            default:
                throw new ValidationException("Неверно указан статус");
        }
    }

    private void userExistence(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
    }
}