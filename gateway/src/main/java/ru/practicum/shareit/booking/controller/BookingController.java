package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String PAGE_SIZE = "10";
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) long userId,
                                             @RequestBody @Valid BookingDTOInput bookingDTOInput) {
        log.info("Получен запрос на бронирование вещи {}", bookingDTOInput);
        return bookingClient.addBooking(userId, bookingDTOInput);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) long userId,
                                                 @PathVariable @Positive long bookingId,
                                                 @RequestParam(name = "approved") boolean approved) {
        log.info("Получен запрос на смену статуса бронирования {} вещи ", bookingId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на получение бронирования {} вещи ", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader(USER_ID) long bookerId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = PAGE_SIZE) @Positive int size,
                                                 @RequestParam(name = "state", defaultValue = "ALL")
                                                 BookingState bookingState) {
        log.info("Получен запрос на получение всех бронирований пользователя ID{}", bookerId);
        return bookingClient.getBookings(bookerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader(USER_ID) long ownerId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = PAGE_SIZE) @Positive int size,
                                                @RequestParam(name = "state", defaultValue = "ALL")
                                                BookingState bookingState) {
        log.info("Получен запрос на получение списка бронирований всех вещей пользователя ID{}", ownerId);
        return bookingClient.getBookingsByOwner(ownerId, bookingState, from, size);
    }
}