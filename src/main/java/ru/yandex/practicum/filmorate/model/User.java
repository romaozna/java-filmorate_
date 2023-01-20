package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
@With
@EqualsAndHashCode
public class User {
    private int id;

    @Email
    @NotBlank
    private final String email;

    @NotBlank
    private final String login;

    private String name;

    @NotNull
    @PastOrPresent
    private final LocalDate birthday;
}
