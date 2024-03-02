package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingDTOInput;

import java.util.List;

public interface BookingService {
    BookingDTO add(Long userId, BookingDTOInput bookingDTOInput);

    BookingDTO approve(Long userId, Long bookingId, boolean approve);

    BookingDTO get(Long userId, Long bookingId);

    List<BookingDTO> getAllByBooker(Long booker, BookingState bookingState);

    List<BookingDTO> getAllByOwner(Long ownerId, BookingState bookingState);
}
