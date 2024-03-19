package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingService bookingService;
    private User owner;
    private User ownerData;
    private User user;
    private User userData;
    private ItemDTO itemDTO;
    private BookingDTOInput bookingDTOInput;

    @BeforeAll
    void init() {
        owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        ownerData = userRepository.save(owner);
        user = User.builder()
                .name("user")
                .email("user@email.com")
                .build();
        userData = userRepository.save(user);
        itemDTO = ItemDTO.builder()
                .name("itemDto")
                .description("itemDto description")
                .available(true)
                .build();
        bookingDTOInput = BookingDTOInput.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @AfterAll
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    void getItemByIdTest() {
        ItemDTO savedItem = itemService.addItem(ownerData.getId(), itemDTO);
        BookingDTO bookingDTO = bookingService.add(userData.getId(), bookingDTOInput);
        ItemDTO itemData = itemService.getItem(1L, 1L);
        assertThat(itemData, notNullValue());
        assertThat(itemData.getId(), is(savedItem.getId()));
        assertThat(itemData.getName(), is(itemDTO.getName()));
        assertThat(itemData.getDescription(), is(itemDTO.getDescription()));
        assertThat(itemData.getNextBooking().getStart(), is(bookingDTOInput.getStart()));
        assertThat(itemData.getNextBooking().getEnd(), is(bookingDTOInput.getEnd()));
        assertThat(itemData.getComments(), empty());
    }
}