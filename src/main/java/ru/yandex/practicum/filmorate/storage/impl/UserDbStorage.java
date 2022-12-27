package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer count() {
        String sqlRequest = "SELECT count(*) FROM users";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> rs.getInt("count"))
                .stream()
                .findFirst()
                .get();
    }

    @Override
    public void deleteAll() {
        String sqlRequest = "DELETE FROM users";
        jdbcTemplate.update(sqlRequest);
    }

    @Override
    public void deleteById(Integer userId) {
        if (isUser(userId)) {
            String sqlRequest = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sqlRequest, userId);
        }
    }

    @Override
    public List<User> findAll() {
        String sqlRequest = "SELECT * FROM users";
        return jdbcTemplate.query(sqlRequest, UserMapper::mapToUser)
                .stream()
                .map(this::setFriends)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Integer userId) {
        String sqlRequest = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sqlRequest, UserMapper::mapToUser, userId)
                .stream()
                .map(this::setFriends)
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == null) {
            String sqlRequest = "INSERT INTO users(login, email, name, birthday) " +
                    "values (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlRequest,
                        new String[]{"user_id"}
                );
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
             user.setId(keyHolder.getKey().intValue());
        } else {
            if (isUser(user.getId())) {
                String sqlRequest = "UPDATE users SET " +
                        "login = ?, email = ?, name = ?, birthday = ?" +
                        "WHERE user_id = ?";
                jdbcTemplate.update(sqlRequest, user.getLogin(),
                        user.getEmail(),
                        user.getName(),
                        Date.valueOf(user.getBirthday()),
                        user.getId()
                );
            }
        }
        return user;
    }
    @Override
    public Optional<List<Integer>> findFriendsByUserId(Integer userId) {
        if (isUser(userId)) {
            String sqlRequest = "SELECT to_user_id FROM friendship " +
                    "WHERE from_user_id = ?";
            List<Integer> idList = jdbcTemplate.queryForList(sqlRequest, Integer.class, userId);
            return Optional.of(idList);
        }
        return Optional.empty();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (isUser(userId) && isUser(friendId)) {
            String sqlRequest = "MERGE INTO friendship(from_user_id, to_user_id) "
                    + "VALUES (?, ?)";
            jdbcTemplate.update(sqlRequest, userId, friendId);
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (isUser(userId) && isUser(friendId)) {
            String sqlRequest = "DELETE FROM friendship " +
                    "WHERE from_user_id = ? AND to_user_id = ?";
            jdbcTemplate.update(sqlRequest, userId, friendId);
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (isUser(userId) && isUser(otherId)) {
            String sqlRequest = "SELECT u.* FROM users AS u "
                    + "JOIN friendship AS f ON u.user_id = f.to_user_id "
                    + "WHERE f.from_user_id = ? "
                    + "INTERSECT "
                    + "SELECT uu.* FROM users AS uu "
                    + "JOIN friendship AS ff ON uu.user_id = ff.to_user_id "
                    + "WHERE ff.from_user_id = ? ";

            return jdbcTemplate.query(sqlRequest, UserMapper::mapToUser, userId, otherId);
        } else {
            return new ArrayList<>();
        }
    }
    private User setFriends(User user) {
        Optional<List<Integer>> friendsId = findFriendsByUserId(user.getId());
        friendsId.ifPresent(integers -> integers
                .forEach(user::addFriend));
        return user;
    };

    private boolean isUser (Integer userId) {
        String sqlRequest = "SELECT count(*) FROM users WHERE user_id = ?";
        Integer result = jdbcTemplate.queryForObject(sqlRequest, Integer.class, userId);
        return result != 0;
    }
}
