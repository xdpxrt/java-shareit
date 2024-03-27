package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class RequestDTO {
    @NotBlank(message = "Необходимо добавить описнаие!")
    private String description;
}