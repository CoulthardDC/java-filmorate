package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public Integer count() {
        return id;
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public void deleteById(Integer id) {
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++id);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<List<Integer>> findFriendsByUserId(Integer userId) {
        if (findById(userId).isPresent()) {
            return Optional.of(new ArrayList<>(users.get(userId).getFriends()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(Integer userId, Integer otherId) {

    }

    @Override
    public void deleteFriend(Integer userId, Integer otherId) {

    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return null;
    }
}
