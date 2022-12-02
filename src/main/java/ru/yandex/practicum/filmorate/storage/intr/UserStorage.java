package ru.yandex.practicum.filmorate.storage.intr;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    User removeUserById(Integer id);
    User updateUserById(Integer id, User user);
    User getUserById(Integer id);
    List<User> getAllUsers();
}
