package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
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
    public UserDTO getUser(@PathVariable(value = "id") long id) {
        log.info("Получен запрос на получение пользователя ID {}", id);
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable(value = "id") long id) {
        log.info("Получен запрос на редактирование пользователя ID {}", id);
        userDTO.setId(id);
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") long id) {
        log.info("Получен запрос на удаление пользователя ID {}", id);
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }
}
