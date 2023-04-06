package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedDao;

import java.util.List;

@Component
public class FeedDaoImpl implements FeedDao {
    private final JdbcTemplate jdbcTemplate;

    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeeds(Integer userId) {
        String sqlRequest = "SELECT * FROM feeds " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sqlRequest, FeedMapper::mapToFeed, userId);
    }

    @Override
    public void addFeed(Integer userId, Event event, Operation operation, Integer entityId) {
        String sqlRequest = "INSERT INTO feeds (user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlRequest, userId, event.toString(), operation.toString(), entityId);
    }
}
