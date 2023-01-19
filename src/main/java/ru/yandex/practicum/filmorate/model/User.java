package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class User {

    private int id;

    @Email(message = "Неверный формат email")
    private final String email;

    @NotBlank(message = "Логин не должен быть пустым")
    private final String login;

    private String name;

    @Past(message = "Дата рождения должна быть в прошлом")
    private final LocalDate birthday;
}
