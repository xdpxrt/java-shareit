package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.user.model.UserDTO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDTO {
    private Long id;
    @NotBlank
    private String description;
    private UserDTO requestor;
    private LocalDateTime created;
    private List<ItemDTO> items;
}