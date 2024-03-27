package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    private UserDTO userDTO;

    @BeforeEach
    void init() {
        userDTO = UserDTO.builder()
                .name("username")
                .email("test@email.com")
                .build();
    }

    @Test
    void addUserTest() {
        UserDTO savedUser = userService.addUser(userDTO);

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getId(), greaterThan(0L));
        assertThat(savedUser.getName(), is(userDTO.getName()));
        assertThat(savedUser.getEmail(), is(userDTO.getEmail()));
    }
}