package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.*;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer  id;
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

    @JsonIgnore
    Set<Integer> likes = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public boolean removeLike(Integer userId) {
        return likes.remove(userId);
    }
}
