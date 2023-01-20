package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@With
@EqualsAndHashCode
public class Film {
    private int id;

    @NotBlank
    private final String name;

    @NotBlank
    @Size(max = 200)
    private final String description;

    @NotNull
    @PastOrPresent
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    @NotNull
    private Mpa mpa;


    private List<Genre> genres;
}
