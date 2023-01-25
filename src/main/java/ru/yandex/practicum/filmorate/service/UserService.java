package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FeedDao feedDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FeedDao feedDao) {
        this.userStorage = userStorage;
        this.feedDao = feedDao;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return findUserOrElseThrow(id);
    }

    public void addUser(User user) {
        userStorage.save(user);
    }

    public void updateUserById(User user) {
        Integer id = user.getId();
        findUserOrElseThrow(id);
        userStorage.save(user);
    }

    public void removeUserById(Integer id) {
        findUserOrElseThrow(id);
        userStorage.deleteById(id);
    }


    public void addToFriend(Integer userId, Integer friendId) {
        findUserOrElseThrow(userId);
        findUserOrElseThrow(friendId);
        userStorage.addFriend(userId, friendId);
        feedDao.addFeed(userId, Event.FRIEND, Operation.ADD, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        findUserOrElseThrow(userId);
        findUserOrElseThrow(friendId);
        userStorage.deleteFriend(userId, friendId);
        feedDao.addFeed(userId, Event.FRIEND, Operation.REMOVE, friendId);
    }

    public List<User> getUserFriend(Integer userId) {
        findUserOrElseThrow(userId);
        return userStorage.findFriendsByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .map(userStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Feed> getFeeds(Integer userId) {
        findUserOrElseThrow(userId);
        return feedDao.getFeeds(userId);
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
