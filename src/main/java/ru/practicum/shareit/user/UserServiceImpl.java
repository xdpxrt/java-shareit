package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO addUser(UserDTO userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank() || userDto.getEmail() == null)
            throw new ValidationException("Не указано имя или почта!");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.addUser(user));
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {
        userRepository.isUserExist(userDto.getId());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.isUserExist(id);
        userRepository.deleteUser(id);
    }

    @Override
    public UserDTO getUser(long id) {
        userRepository.isUserExist(id);
        User user = userRepository.getUser(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
