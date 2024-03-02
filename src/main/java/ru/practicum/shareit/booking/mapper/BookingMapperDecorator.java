package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public abstract class BookingMapperDecorator implements BookingMapper {
    @Autowired
    BookingMapper bookingMapper;

    @Override
    public Booking toBooking(BookingDTOInput bookingDTOInput, Item item, User booker, BookingStatus status) {
        Booking booking = bookingMapper.toBooking(bookingDTOInput, item, booker, status);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}