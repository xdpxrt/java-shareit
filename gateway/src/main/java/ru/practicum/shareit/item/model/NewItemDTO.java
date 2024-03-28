package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewItemDTO {
    @NotBlank(message = "Необходимо указать название!")
    private String name;
    @NotBlank(message = "Необходимо добавить описание!")
    private String description;
    @NotNull(message = "Необходимо указать статус!")
    private Boolean available;
    private Long requestId;
}