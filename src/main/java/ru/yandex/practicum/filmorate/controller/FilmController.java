package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> findAll() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/films");
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable("id") int filmId) {
        log.info("Получен запрос к эндпоинту: {} /films/{}", "GET", filmId);
        return filmService.getFilmById(filmId);
    }

    @GetMapping(value = "/popular")
    public List<Film> findTopFilms(@RequestParam(defaultValue = "10") int count,
                                   @RequestParam(defaultValue = "-1") int genreId,
                                   @RequestParam(defaultValue = "-1") int year) {
        log.info("Получен запрос к эндпоинту: {} /films/{}?count={}&genreId={}&year={}",
                "GET", "popular", count, genreId, year);
        return filmService.getTopFilms(count, genreId, year);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/films");
        filmService.addFilm(film);
        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/films");
        filmService.updateFilmById(film);
        return film;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<?> addLikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен запрос к эндпоинту: {} /films/{}/like/{}", "PUT", filmId, userId);
        filmService.addLikeToFilm(filmId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<Object> removeLikeFromFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен запрос к эндпоинту: {} /films/{}/like/{}", "DELETE", filmId, userId);
        filmService.removeLikeFromFilm(filmId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> removeFilmById(@PathVariable("id") int filmId) {
        log.info("Получен запрос к эндпоинту: {} /films/{}", "DELETE", filmId);
        filmService.removeFilmById(filmId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping(value = "/director/{directorId}")
    public List<Film> findFilmsByDirectorId(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Получен запрос к эндпоинту: {} /films/{}/{}?sortBy={}", "GET", "director", directorId, sortBy);
        return filmService.findFilmsByDirectorId(directorId, sortBy);
    }

    @GetMapping( "/search")
    public List<Film> findFilmsBySearch(@RequestParam String query, @RequestParam List<String> by)
            throws ValidationException {
        String parameterByTitle = "title";
        String parameterByDirector = "director";

        if (query.isEmpty()) {
            throw new ValidationException("Отсутсвует параметр строки запроса query.");
        } else if (by.isEmpty()) {
            throw new ValidationException("Отсутсвует параметр строки запроса by.");
        } else if (by.size() == 1 && (!by.get(0).equals(parameterByTitle) && !by.get(0).equals(parameterByDirector))) {
            throw new ValidationException(String.format("Неправильный параметр строки запроса by = %s.", by.get(0)));
        } else if ((by.size() == 2) && ((!by.get(0).equals(parameterByTitle) && !by.get(0).equals(parameterByDirector))
                && (!by.get(1).equals(parameterByTitle) && !by.get(1).equals(parameterByDirector)))) {
            throw new ValidationException(String.format("Неправильные параметры строки запроса by = %s, %s.", by.get(0),
                    by.get(1)));
        }
        return filmService.findFilmsBySearch(query, by);
    }

    @GetMapping(value = "/common")
    public List<Film> findCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("Получен запрос к эндпоинту: {} /common{}/{}", "GET", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}
