package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.NewUserDTO;
import ru.practicum.shareit.user.model.UpdateUserDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody NewUserDTO userDTO) {
        log.info("Получен запрос на добавление пользователя {}", userDTO);
        return userClient.addUser(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на получение пользователя ID{}", id);
        return userClient.getUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDTO userDTO,
                                             @PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на редактирование пользователя ID{}", id);
        return userClient.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на удаление пользователя ID{}", id);
        userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }
}
