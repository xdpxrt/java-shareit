package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDTO addUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Получен запрос на добавление пользователя {}", userDTO);
        return userService.addUser(userDTO);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на получение пользователя ID{}", id);
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на редактирование пользователя ID{}", id);
        userDTO.setId(id);
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") @Positive long id) {
        log.info("Получен запрос на удаление пользователя ID{}", id);
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }
}
