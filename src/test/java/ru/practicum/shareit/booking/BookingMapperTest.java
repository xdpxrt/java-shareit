package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private final BookingDTOInput bookingDTOInput = BookingDTOInput.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .owner(user)
            .description("description")
            .available(true)
            .build();
    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void toBookingItemDto() {
        Booking booking = bookingMapper.toBooking(bookingDTOInput, item, user, BookingStatus.WAITING);
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}