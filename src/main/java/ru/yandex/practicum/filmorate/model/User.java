package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Builder
@Data
public class User {

    private int id;

    @Email
    private final String email;

    @NotBlank
    private final String login;

    private String name;

    @Past
    private final LocalDate birthday;
}
