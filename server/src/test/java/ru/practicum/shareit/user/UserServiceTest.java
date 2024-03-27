package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private final UserMapper userMapper = new UserMapperImpl();
    private final UserDTO userDTO = UserDTO.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();
    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();
    private final UserDTO newUserDTO = UserDTO.builder()
            .id(1L)
            .name("newUser")
            .email("newUser@user.com")
            .build();
    private final User newUser = User.builder()
            .id(1L)
            .name("newUser")
            .email("newUser@user.com")
            .build();

    @BeforeEach
    void set() {
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void addUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        assertEquals(userDTO, userService.addUser(userDTO));
    }

    @Test
    void addUserNameNotValidTest() {
        UserDTO userBad = UserDTO.builder()
                .id(1L)
                .name("")
                .email("user@user.com")
                .build();
        assertThrows(ValidationException.class, () -> userService.addUser(userBad));
        verify(userRepository, never()).save(userMapper.toUser(userBad));
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(newUser);
        assertEquals(newUserDTO, userService.updateUser(userDTO));
    }

    @Test
    void updateUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> userService.updateUser(userDTO));
        verify(userRepository, never()).save(userMapper.toUser(userDTO));
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        assertEquals(userDTO, userService.getUser(1));
    }

    @Test
    void getUserNotExistTest() {
        assertThrows(NotFoundException.class, () -> userService.getUser(1));

    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(user, newUser);
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(2, userService.getAllUsers().size());
    }
}