package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDTOShort {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}