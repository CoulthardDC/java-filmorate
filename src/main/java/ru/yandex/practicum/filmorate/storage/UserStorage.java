package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Integer count();
    void deleteAll();
    void deleteById(Integer id);
    List<User> findAll();
    Optional<User> findById(Integer id);
    User save(User user);
    Optional<List<Integer>> findFriendsByUserId(Integer userId);
    public void addFriend(Integer userId, Integer friendId);
    public void deleteFriend(Integer userId, Integer friendId);
    public List<User> getCommonFriends(Integer userId, Integer otherId);
}
