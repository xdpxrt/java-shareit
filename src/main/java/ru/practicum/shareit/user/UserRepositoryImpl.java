package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private long countId = 1;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();


    @Override
    public User addUser(User user) {
        isEmailValid(user.getEmail());
        user.setId(getId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        User user = users.get(updateUser.getId());
        if (updateUser.getName() != null) user.setName(updateUser.getName());
        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            isEmailValid(updateUser.getEmail());
            emails.remove(user.getEmail());
            user.setEmail(updateUser.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        String email = users.get(id).getEmail();
        emails.remove(email);
        users.remove(id);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void isUserExist(long id) {
        if (!users.containsKey(id)) throw new NotFoundException(String.format("Пользвателя с ID%d не существует!", id));
    }

    private long getId() {
        return countId++;
    }

    private void isEmailValid(String email) {
        if (emails.contains(email)) throw new ConflictException("Такой email уже занят!");
    }
}
