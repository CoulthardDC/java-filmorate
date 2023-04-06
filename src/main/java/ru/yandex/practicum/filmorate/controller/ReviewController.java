package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public Review postReview(@Valid @RequestBody Review review) throws ValidationException, FilmNotFoundException,
            UserNotFoundException, ReviewNotFoundException {
        return reviewService.create(review);
    }

    @GetMapping(value = { "/reviews/{reviewId}", "/reviews"})
    public Object getReviewS(@PathVariable(required = false) Integer reviewId,
                             @RequestParam(required = false) Integer filmId,
                             @RequestParam(required = false, defaultValue = "10") Integer count)
            throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {

        if (reviewId == null) {
            if (filmId == null) {
                return reviewService.getAllReviews();
            }
            return reviewService.getAllReviewsByFilmId(filmId, count);
        } else {
            return reviewService.getReviewById(reviewId);
        }
    }

    @PutMapping("/reviews")
    public Review putReview(@Valid @RequestBody Review review) throws ReviewNotFoundException, UserNotFoundException {
        return reviewService.update(review);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public Boolean deleteReview(@PathVariable Integer reviewId) throws ReviewNotFoundException {
        return reviewService.deleteReview(reviewId);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public Review putLike(@PathVariable("id") int reviewId, @PathVariable int userId) throws ReviewNotFoundException,
            UserNotFoundException {
        return reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public Review putDislike(@PathVariable("id") int reviewId, @PathVariable int userId) throws ReviewNotFoundException,
            UserNotFoundException {
        return reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public Review deleteLike(@PathVariable("id") int reviewId, @PathVariable int userId) throws ReviewNotFoundException,
            UserNotFoundException {
        return reviewService.deleteLike(reviewId, userId);
    }
    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable("id") int reviewId, @PathVariable int userId)
            throws ReviewNotFoundException, UserNotFoundException {
        return reviewService.deleteDisLike(reviewId, userId);
    }
}