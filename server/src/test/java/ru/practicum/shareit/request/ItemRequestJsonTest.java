package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.request.model.ItemRequestDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDTO> json;

    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemDTO itemDTO = ItemDTO.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();
        ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
                .id(1L)
                .description("description")
                .items(List.of(itemDTO))
                .created(now)
                .build();
        JsonContent<ItemRequestDTO> jsonContent = json.write(itemRequestDTO);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        String json = "{\"id\": 1, \"description\": \"description\", \"items\": [{\"id\": 1, \"name\": \"Item\", " +
                "\"description\": \"Description\", \"available\": true, \"requestId\": 1}], \"created\": " +
                "\"2022-01-01T12:00:00\"}";

        ItemRequestDTO itemRequestDTO = this.json.parseObject(json);

        assertThat(itemRequestDTO.getId()).isEqualTo(1);
        assertThat(itemRequestDTO.getDescription()).isEqualTo("description");
        assertThat(itemRequestDTO.getCreated()).isEqualTo(LocalDateTime.parse("2022-01-01T12:00:00"));
        assertThat(itemRequestDTO.getItems()).hasSize(1);
        assertThat(itemRequestDTO.getItems().get(0).getId()).isEqualTo(1);
        assertThat(itemRequestDTO.getItems().get(0).getName()).isEqualTo("Item");
        assertThat(itemRequestDTO.getItems().get(0).getDescription()).isEqualTo("Description");
        assertThat(itemRequestDTO.getItems().get(0).getAvailable()).isEqualTo(true);
        assertThat(itemRequestDTO.getItems().get(0).getRequestId()).isEqualTo(1);
    }
}