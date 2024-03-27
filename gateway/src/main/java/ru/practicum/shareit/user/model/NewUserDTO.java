package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewUserDTO {
    private long id;
    @NotBlank(message = "Заполните имя пользователя!")
    private String name;
    @NotNull(message = "Заполните email-адрес!")
    @Email(message = "Некорректно указан email-адрес!")
    private String email;
}