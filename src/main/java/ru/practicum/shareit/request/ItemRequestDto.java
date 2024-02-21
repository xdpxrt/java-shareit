package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
}
