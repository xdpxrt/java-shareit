package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")

public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос на добавление вещи {}", itemDTO);
        return itemService.addItem(userId, itemDTO);
    }

    @GetMapping("/{id}")
    public ItemDTO getItem(@PathVariable @Positive long id) {
        log.info("Получен запрос на получение вещи {}", id);
        return itemService.getItem(id);
    }

    @PatchMapping("/{id}")
    public ItemDTO updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDTO itemDTO,
                              @PathVariable @Positive long id) {
        log.info("Получен запрос на обновление вещи {}", id);
        return itemService.updateItem(userId, itemDTO, id);
    }

    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение списка вещей пользователя с ID{}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> findItem(@RequestParam String text) {
        log.info("Получен запрос на получение списка вещей по поиску {}", text);
        return itemService.findItems(text);
    }

}
