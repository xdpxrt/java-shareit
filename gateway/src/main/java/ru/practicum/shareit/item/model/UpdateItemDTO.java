package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateItemDTO {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}