package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDTO toBookingDTO(Booking booking);

    List<BookingDTO> toBookingDTO(List<Booking> booking);

    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookingDTOInput bookingDTOInput, Item item, User booker, BookingStatus status);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    BookingDTOShort toBookingDTOShort(Booking booking);
}