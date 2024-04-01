package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl_;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {
    @InjectMocks
    private BookingMapperImpl decorator;
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
    @Spy
    private final BookingMapper bookingMapper = new BookingMapperImpl_();

    @Test
    void toBookingItemDto() {
        Booking booking = decorator.toBooking(bookingDTOInput, item, user, BookingStatus.WAITING);
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}