package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotBlank
    final String name;
    @Size(max = 200)
    final String description;
    @FilmReleaseDateConstraint
    final LocalDate releaseDate;
    @Positive
    final Long duration;
}
