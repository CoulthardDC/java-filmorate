package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.Duration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.controller.serializer.DurationSerializer;


@Data
public class Film {
    private Integer id;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
