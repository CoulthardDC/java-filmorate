package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return findUserOrElseThrow(id);
    }

    public User addUser(User user) {
        userStorage.save(user);
        return user;
    }

    public User updateUserById(User user) {
        Integer id = user.getId();
        findUserOrElseThrow(id);
        userStorage.save(user);
        return user;
    }

    public void removeUserById(Integer id) {
        findUserOrElseThrow(id);
        userStorage.deleteById(id);
    }


    public void addToFriend(Integer userId, Integer friendId) {
        User user = findUserOrElseThrow(userId);
        User friend = findUserOrElseThrow(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = findUserOrElseThrow(userId);
        User friend = findUserOrElseThrow(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getUserFriend(Integer userId) {
        User user = findUserOrElseThrow(userId);
        return userStorage.findAll()
                .stream()
                .map(User::getId)
                .filter(p -> user.getFriends().contains(p))
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = findUserOrElseThrow(userId);
        User other = findUserOrElseThrow(otherId);
        Set<User> userFriends = userStorage.findAll()
                .stream()
                .map(User::getId)
                .filter(p -> user.getFriends().contains(p))
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Set<User> otherFriends = userStorage.findAll()
                .stream()
                .map(User::getId)
                .filter(p -> other.getFriends().contains(p))
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toSet());
        userFriends.retainAll(otherFriends);
        return new ArrayList<>(userFriends);
    }

    private User findUserOrElseThrow(Integer userId) {
        return userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}
