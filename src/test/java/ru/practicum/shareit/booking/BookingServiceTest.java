package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final LocalDateTime now = LocalDateTime.now();
    private final User owner = User.builder()
            .id(1L)
            .name("owner")
            .email("owner@owner.com")
            .build();
    private final User user = User.builder()
            .id(2L)
            .name("user")
            .email("user@user.com")
            .build();
    private final UserDTO userDTO = UserDTO.builder()
            .id(2L)
            .name("user")
            .email("user@user.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(owner)
            .build();
    private final ItemDTO itemDTO = ItemDTO.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final BookingDTOInput bookingDTOInput = BookingDTOInput.builder()
            .itemId(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .build();
    private final BookingDTO bookingDTO = BookingDTO.builder()
            .id(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(itemDTO)
            .booker(userDTO)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();
    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void set() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);
    }

    @Test
    void addBookingTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        assertEquals(bookingDTO, bookingService.add(2L, bookingDTOInput));
    }

    @Test
    void addBookingWrongDatesTest() {
        BookingDTOInput badBookingDTOInput = BookingDTOInput.builder()
                .start(now.plusDays(1L))
                .end(now.minusDays(2L))
                .build();
        assertThrows(BadRequestException.class, () -> bookingService.add(2L, badBookingDTOInput));
    }

    @Test
    void addBookingUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> bookingService.add(2L, bookingDTOInput));
    }

    @Test
    void addBookingBookerIsOwnerTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> bookingService.add(1L, bookingDTOInput));
    }

    @Test
    void addBookingBookerIsNotExistTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> bookingService.add(2L, bookingDTOInput));
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        bookingDTO.setStatus(BookingStatus.APPROVED);
        assertEquals(bookingDTO, bookingService.approve(1L, 2L, true));
    }

    @Test
    void approveBookingNotExistTest() {
        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void approveBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.approve(2L, 1L, true));
    }

    @Test
    void approveBookingAlreadyApprovedTest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(BadRequestException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertEquals(bookingDTO, bookingService.get(1L, 1L));
    }

    @Test
    void getBookingNotExistTest() {
        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 1L));
    }

    @Test
    void getBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.get(3L, 1L));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getAllByBookerTest(BookingState bookingState) {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        switch (bookingState) {
            case ALL:
                when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case PAST:
                when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case FUTURE:
                when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case CURRENT:
                when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                        any(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case WAITING:
            case REJECTED:
                when(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
        }
        assertEquals(List.of(bookingDTO), bookingService.getAllByBooker(2L, bookingState, pageRequest));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getAllByOwnerUnknownTest(BookingState bookingState) {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        switch (bookingState) {
            case ALL:
                when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case PAST:
                when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case FUTURE:
                when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case CURRENT:
                when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                        any(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
            case WAITING:
            case REJECTED:
                when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(new PageImpl<>(List.of(booking), pageRequest, 10));
                break;
        }
        assertEquals(List.of(bookingDTO), bookingService.getAllByOwner(2L, bookingState, pageRequest));
    }

    @Test
    void getAllByBookerNotExist() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByBooker(2L, BookingState.ALL, pageRequest));
    }
}