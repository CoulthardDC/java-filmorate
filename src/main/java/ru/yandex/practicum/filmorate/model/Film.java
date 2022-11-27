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
    String name;
    @Size(max = 200)
    String description;
    @FilmReleaseDateConstraint
    LocalDate releaseDate;
    @Positive
    Long duration;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
