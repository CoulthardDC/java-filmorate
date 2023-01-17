package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDao;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;

    public Review create(Review review) throws ValidationException, FilmNotFoundException, UserNotFoundException,
            ReviewNotFoundException {
        return reviewDao.create(review);
    }

    public Review getReviewById(int reviewId) throws ReviewNotFoundException {
        return reviewDao.getReviewById(reviewId);
    }

    public Collection<Review> getAllReviews() {
        return reviewDao.getAllReviews();
    }

    public Collection<Review> getAllReviewsByFilmId(int filmId, int count) throws UserNotFoundException,
            FilmNotFoundException {
        return reviewDao.getAllReviewsByFilmId(filmId, count);
    }

    public Review update(Review review) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDao.update(review);
    }
    public Boolean deleteReview(int reviewId) throws ReviewNotFoundException {
        return reviewDao.deleteReview(reviewId);
    }

    public Review addLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDao.addLike(reviewId, userId);
    }
    public Review addDislike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDao.addDislike(reviewId, userId);
    }
    public Review deleteLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDao.deleteLike(reviewId, userId);
    }

    public Review deleteDisLike(int reviewId, int userId) throws ReviewNotFoundException, UserNotFoundException {
        return reviewDao.deleteDisLike(reviewId, userId);
    }
}
