package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        userService = new UserServiceImpl(userRepository, userMapper);
        userDTO = new UserDTO(1, "Name", "email@email.com");
    }

    @Test
    public void addUserTest() {
        assertEquals(userDTO, userService.addUser(userDTO));
    }

    @Test
    public void addUserNoEmailTest() {
        assertThrows(ValidationException.class,
                () -> userService.addUser(new UserDTO(1, "Name", null)));
    }

    @Test
    public void addUserDuplicateEmailTest() {
        userService.addUser(userDTO);
        assertThrows(ConflictException.class,
                () -> userService.addUser(userDTO));
    }

    @Test
    public void updateUserTest() {
        userService.addUser(userDTO);
        UserDTO updateUserDTO = new UserDTO(1, "newName", "newEmail@email.com");
        assertEquals(updateUserDTO, userService.updateUser(updateUserDTO));
    }

    @Test
    public void updateNameTest() {
        userService.addUser(userDTO);
        UserDTO updateUserDTO = new UserDTO(1, "newName", null);
        assertEquals(new UserDTO(1, "newName", "email@email.com"),
                userService.updateUser(updateUserDTO));
    }

    @Test
    public void updateEmailTest() {
        userService.addUser(userDTO);
        UserDTO updateUserDTO = new UserDTO(1, null, "newEmail@email.com");
        assertEquals(new UserDTO(1, "Name", "newEmail@email.com"),
                userService.updateUser(updateUserDTO));
    }

    @Test
    public void updateWithSameEmailTest() {
        userService.addUser(userDTO);
        UserDTO updateUserDTO = new UserDTO(1, null, "email@email.com");
        assertThrows(ValidationException.class,
                () -> userService.addUser(updateUserDTO));
    }

    @Test
    public void updateEmailExistTest() {
        userService.addUser(userDTO);
        userService.addUser(new UserDTO(2, "newName", "newEmail@email.com"));
        UserDTO updateUserDTO = new UserDTO(1, null, "newEmail@email.com");
        assertThrows(ValidationException.class,
                () -> userService.addUser(updateUserDTO));
    }

    @Test
    public void getUserTest() {
        userService.addUser(userDTO);
        assertEquals(userDTO, userService.getUser(1));
    }

    @Test
    public void deleteUserTest() {
        userService.addUser(userDTO);
        userService.deleteUser(1);
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    public void getAllUsersTest() {
        userService.addUser(userDTO);
        assertEquals(1, userService.getAllUsers().size());
        assertEquals(userDTO, userService.getAllUsers().get(0));
    }
}
