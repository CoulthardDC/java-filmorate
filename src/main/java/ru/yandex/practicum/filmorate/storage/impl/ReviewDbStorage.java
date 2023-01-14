package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;

@Repository
@Slf4j
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewMapper = new ReviewMapper(jdbcTemplate);
    }

    public Review create(Review review) throws ReviewNotFoundException {
        /*SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap(film)).intValue();

        if (film.getGenres() != null) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?, ?)",
                    film.getGenres(),
                    film.getGenres().size(),
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genre.getId());
                    });
        }

        if (film.getLikes() != null) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO FILMS_LIKES(FILM_ID, USER_ID) VALUES (?, ?)",
                    film.getLikes(),
                    film.getLikes().size(),
                    (PreparedStatement ps, Integer likeId) -> {
                        ps.setInt(1, filmId);
                        ps.setInt(2, likeId);
                    });
        }

        log.info("Создан фильм: {}", getFilmById(filmId));*/

        return null;
    }
}
