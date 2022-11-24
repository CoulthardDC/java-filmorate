package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/users");
        return new ArrayList<>(users.values());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/users");
        if (user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                ||user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка при добавлении пользователя");
            throw new ValidationException("Ошибка валидации при добавлении пользователя");
        }
        user.setId(++id);
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Имя пользователя {} равно логину пользователя: {}", user.getId(), user.getName());
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен, id: {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/users");
        if (user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                ||user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getId() > id
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка при обновлении пользователя");
            throw new ValidationException("Ошибка валидации при обновлении пользователя");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Имя пользователя {} равно логину пользователя: {}", user.getId(), user.getName());
        }
        users.replace(user.getId(), user);
        return user;
    }
}
