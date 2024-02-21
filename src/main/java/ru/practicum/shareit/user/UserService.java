package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    void deleteUser(long id);

    UserDTO getUser(long id);

    List<UserDTO> getAllUsers();
}
