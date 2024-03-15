package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingDTOShort;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDTO {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDTOShort lastBooking;
    private BookingDTOShort nextBooking;
    private List<CommentDTO> comments;
    private Long requestId;
}
