package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")

public class ItemController {
    private final ItemService itemService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDTO addItem(@RequestHeader(USER_ID) long userId, @RequestBody @Valid ItemDTO itemDTO) {
        log.info("Получен запрос на добавление вещи {}", itemDTO);
        return itemService.addItem(userId, itemDTO);
    }

    @GetMapping("/{id}")
    public ItemDTO getItem(@RequestHeader(USER_ID) long userId, @PathVariable @Positive long id) {
        log.info("Получен запрос на получение вещи {}", id);
        return itemService.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDTO updateItem(@RequestHeader(USER_ID) long userId, @RequestBody @Valid ItemDTO itemDTO,
                              @PathVariable @Positive long id) {
        log.info("Получен запрос на обновление вещи {}", id);
        return itemService.updateItem(userId, itemDTO, id);
    }

    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader(USER_ID) long userId) {
        log.info("Получен запрос на получение списка вещей пользователя с ID{}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> findItem(@RequestParam String text) {
        log.info("Получен запрос на получение списка вещей по поиску {}", text);
        return itemService.findItems(text);
    }
}