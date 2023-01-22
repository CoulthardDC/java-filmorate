package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedDao;
import ru.yandex.practicum.filmorate.storage.ReviewDao;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final FeedDao feedDao;

    public Review create(Review review) throws ValidationException, FilmNotFoundException, UserNotFoundException,
            ReviewNotFoundException {
        Review addedReview = reviewDao.create(review);
        feedDao.addFeed(addedReview.getUserId(), Event.REVIEW, Operation.ADD, addedReview.getReviewId());
        return addedReview;
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
        Review updatedReview = reviewDao.update(review);
        feedDao.addFeed(updatedReview.getUserId(), Event.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }
    public Boolean deleteReview(int reviewId) throws ReviewNotFoundException {
        Review removedReview = getReviewById(reviewId);
        feedDao.addFeed(removedReview.getUserId(), Event.REVIEW, Operation.REMOVE, reviewId);
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
