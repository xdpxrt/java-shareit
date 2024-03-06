package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.user.model.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDTO {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDTO item;
    private UserDTO booker;
    private BookingStatus status;
}
