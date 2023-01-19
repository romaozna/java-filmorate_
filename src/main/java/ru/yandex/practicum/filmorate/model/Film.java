package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Film {

    private int id;

    @NotBlank(message = "Имя не должно быть пустым")
    private final String name;

    private final String description;

    @Past(message = "Дата выпуска должна быть в прошлом")
    private final LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть отличной от нуля")
    private final int duration;

    private int rate;
}
