package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class ReviewMapper {
    public static Review mapRowToReview(ResultSet resultSet) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }

    public static Integer mapRowToUserId(ResultSet resultSet) throws SQLException, UserNotFoundException {
        int userId =  resultSet.getInt("user_id");
        if (userId > 0) {
            return userId;
        } else {
            log.error("Пользователь с id = {} не существует.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id = %s не существует.", userId));
        }
    }
}