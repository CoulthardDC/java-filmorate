package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Integer count();

    void deleteById(Integer id);

    List<User> findAll();

    Optional<User> findById(Integer id);

    User save(User user);

    Optional<List<Integer>> findFriendsByUserId(Integer userId);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<User> getCommonFriends(Integer userId, Integer otherId);

    List<Film> findRecommendations(Integer userId);
}
