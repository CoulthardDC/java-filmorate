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

    public Collection<Review> getAllReviewsByFilmId(int filmId, int count) throws UserNotFoundException {
        return reviewDbStorage.getAllReviewsByFilmId(filmId, count);
    }

    public Review update(Review review) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDbStorage.update(review);
    }

    public Boolean deleteReview(int reviewId) {
        return reviewDbStorage.deleteReview(reviewId);
    }
}
