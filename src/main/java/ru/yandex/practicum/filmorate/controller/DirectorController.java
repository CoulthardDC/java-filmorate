package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/directors");
        return directorService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Director getById(@PathVariable("id") int directorId) {
        log.info("Получен запрос к эндпоинту: {} /directors/{}", "GET", directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/directors");
        return directorService.createDirector(director);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/directors");
        return directorService.updateDirector(director);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") int directorId) {
        log.info("Получен запрос к эндпоинту: {} /directors/{}", "DELETE", directorId);
        directorService.deleteDirectorById(directorId);
    }
}
