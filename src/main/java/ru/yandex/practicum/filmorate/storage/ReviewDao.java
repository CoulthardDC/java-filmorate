package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
@Slf4j
public class ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public ReviewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
        this.filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    public Review create(Review review) throws ValidationException, FilmNotFoundException, UserNotFoundException,
            ReviewNotFoundException {

        userChecker(review.getUserId());
        filmChecker(review.getFilmId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        int reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();

        Review createdReview = getReviewById(reviewId);
        log.info("Оставлен отзыв: {}", createdReview);
        return createdReview;
    }

    public Review getReviewById(int reviewId) throws ReviewNotFoundException {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        Review review;

        try {
            review = jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum)
                    -> ReviewMapper.mapRowToReview(resultSet), reviewId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Отзыв с reviewId = {} не найден.", reviewId);
            throw new ReviewNotFoundException(String.format("Отзыв с reviewId = %s не найден.", reviewId));
        }

        if (review != null) {
            review.setLikes(getLikes(reviewId));
        }
        return review;
    }

    public Collection<Review> getAllReviews() {
        String sqlQuery = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC";
        Collection<Review> reviews = jdbcTemplate.query(sqlQuery, (resultSet, rowNum)
                -> ReviewMapper.mapRowToReview(resultSet));
        reviews.forEach(review -> review.setLikes(getLikes(review.getReviewId())));
        return reviews;
    }

    public Collection<Review> getAllReviewsByFilmId(int filmId, int count) throws UserNotFoundException,
            FilmNotFoundException {
        filmChecker(filmId);

        String sqlQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ? ";
        Collection<Review> reviews = jdbcTemplate.query(sqlQuery, (resultSet, rowNum)
                -> ReviewMapper.mapRowToReview(resultSet), filmId, count);
        reviews.forEach(review -> review.setLikes(getLikes(review.getReviewId())));
        return reviews;
    }

    public Review update(Review review) throws ReviewNotFoundException, UserNotFoundException {
        getReviewById(review.getReviewId());

        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());

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

        Review updatedReview = getReviewById(review.getReviewId());
        log.info("Обновлен отзыв: {}.", updatedReview);
        return updatedReview;
    }

    public Boolean deleteReview(int reviewId) throws ReviewNotFoundException {
        getReviewById(reviewId);

        String sqlQueryForDeleteFromReviewsLikes = "DELETE FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleteFromReviewsLikes, reviewId);

        String sqlQueryForDeleteFromReviews = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

        return jdbcTemplate.update(sqlQueryForDeleteFromReviews, reviewId) > 0;
    }

    public Review addLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        getReviewById(reviewId);
        userChecker(userId);

        String sqlQueryForAddLike = "MERGE INTO REVIEWS_LIKES_DISLIKES KEY(REVIEW_ID, USER_ID) VALUES (?, ?, true)";
        jdbcTemplate.update(sqlQueryForAddLike, reviewId, userId);

        String sqlQueryForUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL + (SELECT COUNT(USER_ID) " +
                "FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ?  AND USER_ID = ? AND IS_LIKE = true) " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQueryForUpdateUseful, reviewId, userId, reviewId);
        log.info("Добавлен лайк отзыву с reviewId = {} от пользователя с userId = {}", reviewId, userId);
        return getReviewById(reviewId);
    }

    public Review addDislike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        getReviewById(reviewId);
        userChecker(userId);

        String sqlQueryForAddLike = "MERGE INTO REVIEWS_LIKES_DISLIKES KEY(REVIEW_ID, USER_ID) VALUES (?, ?, false)";
        jdbcTemplate.update(sqlQueryForAddLike, reviewId, userId);

        String sqlQueryForUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL - (SELECT COUNT(USER_ID) " +
                "FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = false) WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQueryForUpdateUseful, reviewId, userId, reviewId);
        log.info("Добавлен дизлайк отзыву с reviewId = {} от пользователя с userId = {}", reviewId, userId);
        return getReviewById(reviewId);
    }

    public Review deleteLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        getReviewById(reviewId);
        userChecker(userId);

        String sqlQueryForUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL - (SELECT COUNT(USER_ID) " +
                "FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = true) WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQueryForUpdateUseful, reviewId, userId, reviewId);

        String sqlQueryForDeleteLike = "DELETE FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? " +
                "AND IS_LIKE = true";
        jdbcTemplate.update(sqlQueryForDeleteLike, reviewId, userId);
        log.info("Удален лайк от пользователя с userId = {} отзыву с reviewId = {}", userId, reviewId);

        return getReviewById(reviewId);
    }
    public Review deleteDisLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        getReviewById(reviewId);
        userChecker(userId);

        String sqlQueryForUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL - (SELECT COUNT(USER_ID) " +
                "FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = false) WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQueryForUpdateUseful, reviewId, userId, reviewId);

        String sqlQueryForDeleteDislike = "DELETE FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? " +
                "AND IS_LIKE = false";
        jdbcTemplate.update(sqlQueryForDeleteDislike, reviewId, userId);
        log.info("Удален дизлайк от пользователя с userId = {} отзыву с reviewId = {}", userId, reviewId);

        return getReviewById(reviewId);
    }

    private void deleteUsersByReviewId(int reviewId) {
        String sqlQueryForDeleteFriends = "DELETE FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ?";
            jdbcTemplate.update(sqlQueryForDeleteFriends, reviewId);

        log.info("Удалены лайки и дизлайки у отзыва с id = {}", reviewId);
    }

    private Map<Boolean, Set<Integer>> getLikes (int reviewId) throws UserNotFoundException {

        Map<Boolean, Set<Integer>> likes = new TreeMap<>();

        String sqlQueryForLikesFalse = "SELECT * FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? " +
                "AND IS_LIKE = false";
        String sqlQueryForLikesTrue = "SELECT * FROM REVIEWS_LIKES_DISLIKES WHERE REVIEW_ID = ? AND IS_LIKE = true";

        Set<Integer> usersForLikesFalse = new TreeSet<>(jdbcTemplate.query(sqlQueryForLikesFalse,
                (resultSetForLikesFalse, rowNumForLikesFalse) -> ReviewMapper.mapRowToUserId(resultSetForLikesFalse),
                reviewId));

        likes.put(false, usersForLikesFalse);

        Set<Integer> usersForLikesTrue = new TreeSet<>(jdbcTemplate.query(sqlQueryForLikesTrue,
                (resultSetForLikesTrue, rowNumForLikesTrue) -> ReviewMapper.mapRowToUserId(resultSetForLikesTrue),
                reviewId));

        likes.put(true, usersForLikesTrue);
        return likes;
    }

    private void filmChecker(int filmId) throws FilmNotFoundException {
        if (filmId == 0) {
            log.error("Отсутствует filmId.");
            throw new ValidationException("Отсутствует filmId.");
        }

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с filmId = {} отсутствует.", filmId);
            throw new FilmNotFoundException(String.format("Фильм с filmId = %s отсутствует.", filmId));
        }
    }
    private void userChecker(int userId) throws UserNotFoundException {
        if (userId == 0) {
            log.error("Отсутствует userId.");
            throw new ValidationException("Отсутствует userId.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользователь с userId = {} отсутствует.", userId);
            throw new UserNotFoundException(String.format("Пользователь с userId = %s отсутствует.", userId));
        }
    }
}