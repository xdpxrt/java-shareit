package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.validator.DateRange;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@DateRange(start = "start", end = "end")
public class BookingDTOInput {
    @NotNull(message = "Не указан ID вещи")
    private long itemId;
    @NotNull(message = "Не указана дата начала бронирования")
    private LocalDateTime start;
    @NotNull(message = "Не указана дата начала бронирования")
    private LocalDateTime end;
}