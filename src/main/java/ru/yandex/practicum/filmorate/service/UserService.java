package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.intr.UserStorage;

import java.util.ArrayList;
import java.util.List;
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

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User removeUserById(Integer id) {
        User removedUser = userStorage.removeUserById(id);
        if (removedUser == null) {
            log.warn("Юзер с id = {} не найден", id);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", id));
        } else {
            return removedUser;
        }
    }

    public User updateUserById(User user) {
        User updatedUser = userStorage.updateUserById(user.getId(), user);
        if (updatedUser == null) {
            log.warn("Юзер с id = {} не найден", user.getId());
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", user.getId()));
        } else {
            return updatedUser;
        }
    }

    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Юзер с id = {} не найден", id);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", id));
        } else {
            return user;
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addToFriend(Integer userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null || userStorage.getUserById(friendId) == null) {
            log.warn("Юзер с id = {} не найден", userId);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", userId));
        } else {
            userStorage.getUserById(userId).addFriend(friendId);
            userStorage.getUserById(friendId).addFriend(userId);
            return userStorage.getUserById(friendId);
        }
    }

    public User removeFriend(Integer userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null || userStorage.getUserById(friendId) == null) {
            log.warn("Юзер с id = {} не найден", userId);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", userId));
        } else {
            userStorage.getUserById(userId).removeFriend(friendId);
            userStorage.getUserById(friendId).removeFriend(userId);
            return userStorage.getUserById(friendId);
        }
    }

    public List<User> getUserFriend(Integer userId) {
        if (userStorage.getUserById(userId) == null) {
            log.warn("Юзер с id = {} не найден", userId);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", userId));
        } else {
            return userStorage.getAllUsers()
                    .stream()
                    .map(User::getId)
                    .filter(p -> userStorage.getUserById(userId).getFriendsIds().contains(p))
                    .map(userStorage::getUserById)
                    .collect(Collectors.toList());
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (userStorage.getUserById(userId) == null || userStorage.getUserById(otherId) == null) {
            log.warn("Юзер с id = {} не найден", userId);
            throw new InvalidIdException(String.format("Юзер с id = %d не найден", userId));
        } else {
            Set<User> userFriends = userStorage.getAllUsers()
                    .stream()
                    .map(User::getId)
                    .filter(p -> userStorage.getUserById(userId).getFriendsIds().contains(p))
                    .map(userStorage::getUserById)
                    .collect(Collectors.toSet());
            Set<User> otherFriends = userStorage.getAllUsers()
                    .stream()
                    .map(User::getId)
                    .filter(p -> userStorage.getUserById(otherId).getFriendsIds().contains(p))
                    .map(userStorage::getUserById)
                    .collect(Collectors.toSet());
            userFriends.retainAll(otherFriends);
            return new ArrayList<>(userFriends);
        }
    }
}
