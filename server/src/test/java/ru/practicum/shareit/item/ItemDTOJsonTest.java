package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemDTO;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDTOJsonTest {
    @Autowired
    private JacksonTester<ItemDTO> json;

    @Test
    void itemDTOTest() throws IOException {
        ItemDTO itemDTO = ItemDTO.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        JsonContent<ItemDTO> jsonContent = json.write(itemDTO);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testDeserializeItemDto() throws IOException {
        String jsonStr = "{\"id\": 1, \"name\": \"Item\", \"description\": \"Description\", \"available\": true}";
        ItemDTO itemDTO = json.parseObject(jsonStr);

        assertThat(itemDTO.getId()).isEqualTo(1);
        assertThat(itemDTO.getName()).isEqualTo("Item");
        assertThat(itemDTO.getDescription()).isEqualTo("Description");
        assertThat(itemDTO.getAvailable()).isEqualTo(true);
    }
}