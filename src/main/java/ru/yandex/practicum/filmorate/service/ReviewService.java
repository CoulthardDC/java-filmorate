package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    public Review create(Review review) throws ReviewNotFoundException {
        return reviewDbStorage.create(review);
    }
}
