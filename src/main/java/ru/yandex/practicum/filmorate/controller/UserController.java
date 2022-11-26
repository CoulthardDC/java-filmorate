package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/users");
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен, id: {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/users");
        if (user.getId() > id) {
            log.warn("Ошибка при обновлении пользователя");
            throw new ValidationException("Ошибка валидации при обновлении пользователя");
        }
        users.replace(user.getId(), user);
        return user;
    }
}
