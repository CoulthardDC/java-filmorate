package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
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

    @GetMapping(value = "/{id}/feed")
    public List<Feed> findFeedsByUserId(@PathVariable("id") int userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}/feeds", "GET", userId);
        return userService.getFeeds(userId);
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
    public ResponseEntity<?> addToFriends(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Получение запроса к эндпоинту: {} users/{}/friends/{}", "PUT", userId, friendId);
        userService.addToFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<Object> removeFromFriends(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Получение запроса к эндпоинту: {} users/{}/friends/{}", "DELETE", userId, friendId);
        userService.removeFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> removeUserBuId(@PathVariable("id") int userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}", "DELETE", userId);
        userService.removeUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping(value = "/{id}/recommendations")
    public List<Film> findRecommendations(@PathVariable("id") int userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}/recommendations", "GET", userId);
        return userService.findRecommendations(userId);
    }
}