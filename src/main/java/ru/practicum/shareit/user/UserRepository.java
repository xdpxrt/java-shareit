package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    User getUser(long id);

    List<User> getAllUsers();

    void isUserExist(long id);
}
