package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedMapper {
    public static Feed mapToFeed(ResultSet rs, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getInt("event_id"));
        feed.setUserId(rs.getInt("user_id"));
        feed.setTimestamp(rs.getTimestamp("time").toInstant().toEpochMilli());
        feed.setEventType(Event.valueOf(rs.getString("event_type")));
        feed.setOperation(Operation.valueOf(rs.getString("operation")));
        feed.setEntityId(rs.getInt("entity_id"));
        return feed;
    }
}
