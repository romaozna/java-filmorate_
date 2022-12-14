package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Builder
@Data
public class Film {

    private int id;

    @NotBlank
    final private String name;

    final private String description;

    final private LocalDate releaseDate;

    final private int duration;

}
