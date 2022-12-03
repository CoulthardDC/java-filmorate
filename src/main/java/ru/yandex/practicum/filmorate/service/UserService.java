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
        return userStorage.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User addUser(User user) {
        userStorage.save(user);
        return user;
    }

    public User updateUserById(User user) {
        Integer id = user.getId();
        userStorage.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userStorage.save(user);
        return user;
    }

    public void removeUserById(Integer id) {
        userStorage.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userStorage.deleteById(id);
    }


    public void addToFriend(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        User friend = userStorage.findById(friendId).orElseThrow(
                () -> new UserNotFoundException(friendId));
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        User friend = userStorage.findById(friendId).orElseThrow(
                () -> new UserNotFoundException(friendId));
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getUserFriend(Integer userId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        return userStorage.findAll()
                .stream()
                .map(User::getId)
                .filter(p -> user.getFriends().contains(p))
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        User other = userStorage.findById(otherId).orElseThrow(
                () -> new UserNotFoundException(otherId));
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
}
