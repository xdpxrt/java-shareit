package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.UserDTO;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDTO> json;
    private final UserDTO userDto = new UserDTO(1L, "user", "user@user.com");

    @Test
    void testSerializeUserDto() throws Exception {
        JsonContent<UserDTO> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String jsonContent = "{\"id\": 1, \"email\": \"user@user.com\", \"name\": \"user\"}";
        UserDTO expectedUserDto = new UserDTO(1L, "user", "user@user.com");
        UserDTO result = json.parseObject(jsonContent);

        assertThat(result).isEqualTo(expectedUserDto);
    }
}