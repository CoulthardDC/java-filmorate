package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public List<Film> findTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос к эндпоинту: {} /films/{}?count={}", "GET", "popular", count);
        return filmService.getTopFilms(count);

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

}
