package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO userDTO);

    UserDTO toUserDto(User user);
}
