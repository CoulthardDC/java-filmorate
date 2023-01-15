package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    public Review create(Review review) throws ValidationException, FilmNotFoundException, UserNotFoundException {
        return reviewDbStorage.create(review);
    }

    public Review getReviewById(int reviewId) throws UserNotFoundException {
        return reviewDbStorage.getReviewById(reviewId);
    }

    public Collection<Review> getAllReviews() throws UserNotFoundException {
        return reviewDbStorage.getAllReviews();
    }

    public Collection<Review> getAllReviewsByFilmId(int filmId, int count) throws UserNotFoundException
            , FilmNotFoundException {
        return reviewDbStorage.getAllReviewsByFilmId(filmId, count);
    }

    public Review update(Review review) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.update(review);
    }
    public Boolean deleteReview(int reviewId) throws ReviewNotFoundException {
        return reviewDbStorage.deleteReview(reviewId);
    }

    public Review addLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.addLike(reviewId, userId);
    }
    public Review addDislike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.addDislike(reviewId, userId);
    }
    public Review deleteLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.deleteLike(reviewId, userId);
    }

    public Review deleteDisLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.deleteDisLike(reviewId, userId);
    }
}
