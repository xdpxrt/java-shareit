package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDTO addItemRequest(@RequestHeader(USER_ID) long userId,
                                         @RequestBody @Valid ItemRequestDTO requestDTO) {
        log.info("Получен запрос на добавление вещи {}", requestDTO);
        return itemRequestService.addItemRequest(userId, requestDTO);
    }

    @GetMapping
    public List<ItemRequestDTO> getOwnerItemRequests(@RequestHeader(USER_ID) long userId) {
        log.info("Получен запрос на получение списка запросов пользователя {}", userId);
        return itemRequestService.getOwnerItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDTO> getItemRequests(@RequestHeader(USER_ID) long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение списка всех запросов");
        return itemRequestService.getItemRequests(userId,
                PageRequest.of(from / size, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDTO getItemRequest(@RequestHeader(USER_ID) long userId,
                                         @PathVariable @Positive long requestId) {
        log.info("Получен запрос на получение запроса с ID{}", requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }
}