package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.NewItemDTO;
import ru.practicum.shareit.item.model.UpdateItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String PAGE_SIZE = "10";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) long userId,
                                          @RequestBody @Valid NewItemDTO itemDTO) {
        log.info("Получен запрос на добавление вещи {}", itemDTO);
        return itemClient.addItem(userId, itemDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long userId,
                                          @PathVariable @Positive long id) {
        log.info("Получен запрос на получение вещи ID{}", id);
        return itemClient.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) long userId,
                                             @RequestBody @Valid UpdateItemDTO itemDTO,
                                             @PathVariable @Positive long id) {
        log.info("Получен запрос на обновление вещи ID{}", id);
        return itemClient.updateItem(userId, id, itemDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID) long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = PAGE_SIZE) @Positive int size) {
        log.info("Получен запрос на получение списка вещей пользователя с ID{}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItem(@RequestHeader(USER_ID) long userId,
                                           @RequestParam String text,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = PAGE_SIZE) @Positive int size) {
        log.info("Получен запрос на получение списка вещей по поиску {}", text);
        return itemClient.findItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long userId,
                                             @PathVariable @Positive long itemId,
                                             @RequestBody @Valid CommentDTO commentDTO) {
        log.info("Получен запрос на добавление комметнария к вещи с ID{}", itemId);
        return itemClient.addComment(userId, itemId, commentDTO);
    }
}