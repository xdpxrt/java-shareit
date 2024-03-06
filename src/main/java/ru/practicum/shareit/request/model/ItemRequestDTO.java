package ru.practicum.shareit.request.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDTO {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
}
