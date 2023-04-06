package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.*;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotBlank()
    String name;
    @Size(max = 200)
    @NotEmpty()
    String description;
    @NotNull()
    @FilmReleaseDateConstraint()
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull()
    @Positive()
    Long duration;
    @NotNull
    Mpa mpa = new Mpa();
    Set<Review> reviews = new TreeSet<>();

    Set<Integer> likes = new HashSet<>();
    Set<Genre> genres = new HashSet<>();
    Set<Director> directors = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        directors.add(director);
    }
}
