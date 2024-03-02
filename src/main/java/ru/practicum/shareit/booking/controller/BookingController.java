package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO add(@RequestHeader(USER_ID) long userId, @Valid @RequestBody BookingDTOInput bookingDTOInput) {
        log.info("Получен запрос на бронирование вещи {}", bookingDTOInput);
        return bookingService.add(userId, bookingDTOInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO approve(@RequestHeader(USER_ID) long userId, @PathVariable @Positive long bookingId,
                              @RequestParam(name = "approved") boolean approved) {
        log.info("Получен запрос на смену статуса бронирования {} вещи ", bookingId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDTO get(@RequestHeader(USER_ID) long userId, @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на получение бронирования {} вещи ", bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDTO> getAllByBooker(@RequestHeader(USER_ID) long bookerId,
                                           @RequestParam(name = "state", defaultValue = "ALL")
                                           BookingState bookingState) {
        log.info("Получен запрос на получение всех бронирований пользователя {}", bookerId);
        return bookingService.getAllByBooker(bookerId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDTO> getAllByOwner(@RequestHeader(USER_ID) long ownerId,
                                          @RequestParam(name = "state", defaultValue = "ALL")
                                          BookingState bookingState) {
        log.info("Получен запрос на получение списка бронирований всех вещей пользователя {}", ownerId);
        return bookingService.getAllByOwner(ownerId, bookingState);
    }
}
