package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);


    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/films");
        return new ArrayList<>(films.values());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/films");
        if (film.getName().isBlank()
                || film.getDescription().length() > MAX_LENGTH
                || film.getDuration().isNegative()
                || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка при добавлении фильма");
            throw new ValidationException("Ошибка валидации при добавлении фильма");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм '{}' добавлен, id: {}", film.getName(), film.getId());
        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/films");
        if (film.getName().isBlank()
                || film.getDescription().length() > MAX_LENGTH
                || film.getDuration().isNegative()
                || film.getId() > id
                || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка при обновлении фильма");
            throw new ValidationException("Ошибка валидации при обновлении фильма");
        }
        films.replace(film.getId(), film);
        log.info("Фильм '{}', id: {} обновлен", film.getName(), film.getId());
        return film;
    }


}
