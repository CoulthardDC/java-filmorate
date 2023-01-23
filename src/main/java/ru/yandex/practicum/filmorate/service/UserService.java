package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return findUserOrElseThrow(id);
    }

    public User addUser(User user) {
        return userStorage.save(user);
    }

    public User updateUserById(User user) {
        Integer id = user.getId();
        findUserOrElseThrow(id);
        return userStorage.save(user);
    }

    public void removeUserById(Integer id) {
        findUserOrElseThrow(id);
        userStorage.deleteById(id);
    }


    public void addToFriend(Integer userId, Integer friendId) {
        User user = findUserOrElseThrow(userId);
        User friend = findUserOrElseThrow(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = findUserOrElseThrow(userId);
        User friend = findUserOrElseThrow(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriend(Integer userId) {
        User user = findUserOrElseThrow(userId);
        return userStorage.findFriendsByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<Film> findRecommendations(Integer userId) {
        findUserOrElseThrow(userId);
        return userStorage.findRecommendations(userId);
    }

    private User findUserOrElseThrow(Integer userId) {
        return userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}
