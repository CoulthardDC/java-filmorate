package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
@Slf4j
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
        this.filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    public Review create(Review review) throws ValidationException, FilmNotFoundException, UserNotFoundException {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Данные отсутствуют.");
        }
        if (userDbStorage.findById(review.getUserId()).isEmpty()) {
            throw new UserNotFoundException("Пользователь с таким id отсутствует.");
        }
        if (filmDbStorage.findById(review.getFilmId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм с таким id отсутствует.");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        int reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap(review)).intValue();

        Map<Boolean, Set<Integer>> likes = review.getLikes();

        if (likes != null) {
            likes.forEach((isLike, usersId) -> jdbcTemplate.batchUpdate(
                    "INSERT INTO REVIEWS_LIKES_DISLIKES(REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, ?)",
                    usersId,
                    usersId.size(),
                    (PreparedStatement ps, Integer idUser) -> {
                        ps.setInt(1, reviewId);
                        ps.setInt(2, idUser);
                        ps.setBoolean(3, isLike);
                    }));
        }

        log.info("Оставлен отзыв: {}", getReviewById(reviewId));
        return getReviewById(reviewId);
    }

    public Review getReviewById(int reviewId) throws UserNotFoundException {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        Review review = jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum)
                -> ReviewMapper.mapRowToReview(resultSet), reviewId);
        if (review != null) {
            review.setLikes(getLikes(reviewId));
        }
        return review;
    }

    public Collection<Review> getAllReviews() throws UserNotFoundException {
        String sqlQuery = "SELECT * FROM REVIEWS";
        Collection<Review> reviews = jdbcTemplate.query(sqlQuery, (resultSet, rowNum)
                -> ReviewMapper.mapRowToReview(resultSet));
        reviews.forEach(review -> review.setLikes(getLikes(review.getReviewId())));
        return reviews;
    }

    public Collection<Review> getAllReviewsByFilmId(int filmId, int count) throws UserNotFoundException {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ? LIMIT ?";
        Collection<Review> reviews = jdbcTemplate.query(sqlQuery, (resultSet, rowNum)
                -> ReviewMapper.mapRowToReview(resultSet), filmId, count);
        reviews.forEach(review -> review.setLikes(getLikes(review.getReviewId())));
        return reviews;
    }

    public Review update(Review review) throws ReviewNotFoundException, UserNotFoundException {
        if (review.getReviewId() < 1) {
            log.error("Отзыв с id = {} не найден.", review.getReviewId());
            throw new ReviewNotFoundException(String.format("Отзыв с id = %s не найден.", review.getReviewId()));
        }

        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?, USER_ID = ?, FILM_ID = ?, USEFUL = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getUserId()
                , review.getFilmId() , review.getUseful(), review.getReviewId());

        deleteUsersByReviewId(review.getReviewId());

        Map<Boolean, Set<Integer>> likes = review.getLikes();
        if (likes != null) {
            likes.forEach((isLike, usersId) -> jdbcTemplate.batchUpdate(
                    "MERGE INTO REVIEWS_LIKES_DISLIKES(REVIEW_ID, USER_ID, IS_LIKE) KEY (REVIEW_ID, USER_ID) " +
                            "VALUES (?, ?, ?)",
                    usersId,
                    usersId.size(),
                    (PreparedStatement ps, Integer idUser) -> {
                        ps.setInt(1, review.getReviewId());
                        ps.setInt(2, idUser);
                        ps.setBoolean(3, isLike);
                    }));
        }

        log.info("Отзыв с id = {} обновлен.", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    public Boolean deleteReview(int reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        return jdbcTemplate.update(sqlQuery, reviewId) > 0;
    }

    private Map<Boolean, Set<Integer>> getLikes (int reviewId) throws UserNotFoundException {
        if (reviewId > 0) {
            Map<Boolean, Set<Integer>> likes = new TreeMap<>();

            String sqlQueryForLikesFalse = "SELECT * FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? " +
                    "AND IS_LIKE = false";
            String sqlQueryForLikesTrue = "SELECT * FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND IS_LIKE = true";

            Set<Integer> usersForLikesFalse = new TreeSet<>(jdbcTemplate.query(sqlQueryForLikesFalse
                    , (resultSetForLikesFalse, rowNumForLikesFalse)
                            -> ReviewMapper.mapRowToUserId(resultSetForLikesFalse), reviewId));

            likes.put(false, usersForLikesFalse);

            Set<Integer> usersForLikesTrue = new TreeSet<>(jdbcTemplate.query(sqlQueryForLikesTrue
                    , (resultSetForLikesTrue, rowNumForLikesTrue)
                            -> ReviewMapper.mapRowToUserId(resultSetForLikesTrue), reviewId));

            likes.put(true, usersForLikesTrue);
            return likes;
        } else {
            log.error("Отзыв с id = {} не найден.", reviewId);
            throw new ReviewNotFoundException(String.format("Отзыв с id = %s не найден.", reviewId));
        }
    }

    public void deleteUsersByReviewId(int reviewId) {
        String sqlQueryForDeleteFriends = "DELETE FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ?";
            jdbcTemplate.update(sqlQueryForDeleteFriends, reviewId);

        log.info("Удалены лайки и дизлайки у отзыва с id = {}", reviewId);
    }
}
