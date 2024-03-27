package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.model.RequestDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String PAGE_SIZE = "10";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(USER_ID) long userId,
                                                 @RequestBody @Valid RequestDTO requestDTO) {
        log.info("Получен запрос на добавление вещи {}", requestDTO);
        return requestClient.addItemRequest(userId, requestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItemRequests(@RequestHeader(USER_ID) long userId) {
        log.info("Получен запрос на получение списка запросов пользователя {}", userId);
        return requestClient.getOwnerItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader(USER_ID) long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = PAGE_SIZE) @Positive int size) {
        log.info("Получен запрос на получение списка всех запросов");
        return requestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID) long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("Получен запрос на получение запроса с ID{}", requestId);
        return requestClient.getItemRequest(userId, requestId);
    }
}