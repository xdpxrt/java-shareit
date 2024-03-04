package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final String USER_NOT_FOUND = "Пользвателя с ID%d не существует!";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO addUser(UserDTO userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank() || userDto.getEmail() == null)
            throw new ValidationException("Не указано имя или почта!");
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userDTO.getId())));
        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, id)));
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO getUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, id)));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}