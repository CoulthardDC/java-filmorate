package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAll() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/users");
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable(value = "id") int userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}", "GET", userId);
        return userService.getUserById(userId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> findFriendsOfUser(@PathVariable(value = "id") int userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}/friends", "GET", userId);
        return userService.getUserFriend(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") int userId, @PathVariable int otherId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}/friends/common/{}", "GET", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/users");
        userService.addUser(user);
        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: {} {}", "PUT", "/users");
        userService.updateUserById(user);
        return user;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Получение запроса к эндпоинту: {} /{}/friends/{}", "PUT", userId, friendId);
        return userService.addToFriend(userId, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User removeFromFriends(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Получение запроса к эндпоинту: {} /{}/friends/{}", "DELETE", userId, friendId);
        return userService.removeFriend(userId, friendId);
    }


}
