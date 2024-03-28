package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDTO addItem(@RequestHeader(USER_ID) long userId,
                           @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос на добавление вещи {}", itemDTO);
        return itemService.addItem(userId, itemDTO);
    }

    @GetMapping("/{id}")
    public ItemDTO getItem(@RequestHeader(USER_ID) long userId,
                           @PathVariable long id) {
        log.info("Получен запрос на получение вещи ID{}", id);
        return itemService.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDTO updateItem(@RequestHeader(USER_ID) long userId,
                              @RequestBody ItemDTO itemDTO,
                              @PathVariable long id) {
        log.info("Получен запрос на обновление вещи ID{}", id);
        return itemService.updateItem(userId, itemDTO, id);
    }

    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader(USER_ID) long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на получение списка вещей пользователя с ID{}", userId);
        return itemService.getAllItems(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/search")
    public List<ItemDTO> findItem(@RequestParam String text,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на получение списка вещей по поиску {}", text);
        return itemService.findItems(text, PageRequest.of(from / size, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@RequestHeader(USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDTO commentDTO) {
        log.info("Получен запрос на добавление комметнария к вещи с ID{}", itemId);
        return itemService.addComment(userId, itemId, commentDTO);
    }
}