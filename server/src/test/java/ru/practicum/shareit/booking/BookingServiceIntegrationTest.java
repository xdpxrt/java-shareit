package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User owner;
    private User booker;
    private Item savedItem;
    private BookingDTOInput bookingDTO;

    @BeforeAll
    void init() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build();
        owner = userRepository.save(user1);
        User user2 = User.builder()
                .name("booker")
                .email("booker@mail.com")
                .build();
        booker = userRepository.save(user2);
        Item item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        savedItem = itemRepository.save(item);
        bookingDTO = BookingDTOInput.builder()
                .itemId(savedItem.getId())
                .start(now.minusDays(2))
                .end(now.plusDays(4))
                .build();
    }

    @AfterAll
    public void cleanDb() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void addBookingTest() {
        BookingDTO addedBooking = bookingService.add(booker.getId(), bookingDTO);
        assertThat(addedBooking.getId(), notNullValue());
        assertThat(addedBooking.getItem().getId(), is(savedItem.getId()));
        assertThat(addedBooking.getStart(), notNullValue());
        assertThat(addedBooking.getEnd(), notNullValue());
        assertThat(addedBooking.getStatus(), is(BookingStatus.WAITING));
    }
}