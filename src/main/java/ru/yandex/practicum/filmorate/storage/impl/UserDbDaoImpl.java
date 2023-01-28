package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.mapper.LikeExtractor;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import ru.yandex.practicum.filmorate.storage.UserDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDbDaoImpl")
public class UserDbDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDao filmDao;

    public UserDbDaoImpl(JdbcTemplate jdbcTemplate, @Qualifier("filmDbDaoImpl") FilmDao filmDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDao = filmDao;
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
    public void deleteById(Integer userId) {
        if (isUser(userId)) {
            String sqlRequest = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sqlRequest, userId);
        }
    }

    @Override
    public List<User> findAll() {
        String sqlRequest = "SELECT * FROM users";
        List<User> result = jdbcTemplate.query(sqlRequest, UserMapper::mapToUser);
        setFriends(result);
        return result;
    }

    @Override
    public Optional<User> findById(Integer userId) {
        String sqlRequest = "SELECT * FROM users WHERE user_id = ?";
        List<User> result = jdbcTemplate.query(sqlRequest, UserMapper::mapToUser, userId);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            setFriends(result);
            return result.stream().findFirst();
        }

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

    @Override
    public List<Film> findRecommendations(Integer userId) {
        String sqlRequest = "SELECT user_id, film_id " +
                "FROM likes;";
        Map<Integer, Set<Integer>> allUsersLikes = jdbcTemplate.query(sqlRequest, new LikeExtractor());

        Set<Integer> userLikes = null;
        if (allUsersLikes != null) {
            userLikes = allUsersLikes.get(userId);
        }
        if (userLikes == null || userLikes.isEmpty()) {
            return new ArrayList<>();
        }
        allUsersLikes.remove(userId);

        Set<Integer> recommendedFilmIds = getRecommendedFilmIds(allUsersLikes, userLikes);

        return recommendedFilmIds.stream()
                .map(id -> filmDao.findById(id).orElseThrow(() -> new FilmNotFoundException(id)))
                .collect(Collectors.toList());
    }

    private Set<Integer> getRecommendedFilmIds(Map<Integer, Set<Integer>> allUsersLikes, Set<Integer> userLikes) {
        int maxCommonCount = 0;
        Set<Integer> recommendedFilmIds = new HashSet<>();
        for (Map.Entry<Integer, Set<Integer>> entry : allUsersLikes.entrySet()) {
            Set<Integer> common = new HashSet<>(userLikes);
            Set<Integer> filmIds = entry.getValue();
            common.retainAll(filmIds);

            if (common.size() > maxCommonCount) {
                maxCommonCount = common.size();
                filmIds.removeAll(common);
                recommendedFilmIds = filmIds;
            }
        }
        return recommendedFilmIds;
    }

    private void setFriends(List<User> users) {
        List<Integer> usersId = users
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        String inSql = String.join(",", Collections.nCopies(usersId.size(), "?"));

        String sqlRequest = "SELECT from_user_id, to_user_id FROM friendship " +
                "WHERE from_user_id in (%s)";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs -> {
            userMap.get(rs.getInt("from_user_id"))
                    .addFriend(rs.getInt("to_user_id"));
            }, usersId.toArray());
    }

    private boolean isUser(Integer userId) {
        String sqlRequest = "SELECT count(*) FROM users WHERE user_id = ?";
        Integer result = jdbcTemplate.queryForObject(sqlRequest, Integer.class, userId);
        return result != 0;
    }
}
