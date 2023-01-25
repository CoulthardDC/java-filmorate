package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FeedDao;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDao directorDao;
    private final FeedDao feedDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       DirectorDao directorDao,
                       FeedDao feedDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        return findFilmOrElseThrow(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.save(film);
    }

    public Film updateFilmById(Film film) {
        Integer id = film.getId();
        findFilmOrElseThrow(id);
        filmStorage.save(film);
        return film;
    }

    public List<Film> findFilmsByDirectorId(Integer directorId, String sortBy) {
        findDirectorOrElseThrow(directorId);
        if (sortBy.equals("year") || sortBy.equals("likes")) {
            return filmStorage.findFilmsByDirectorId(directorId, sortBy);
        } else {
            throw new InvalidParameter(String.format("Невалидное значение sortBy: %s", sortBy));
        }
    }

    public void removeFilmById(Integer id) {
        findFilmOrElseThrow(id);
        filmStorage.deleteById(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        Film film = findFilmOrElseThrow(filmId);
        User user = findUserOrElseThrow(userId);
        filmStorage.addLike(filmId, userId);
        feedDao.addFeed(userId, Event.LIKE, Operation.ADD, filmId);
    }

    public void removeLikeFromFilm(Integer filmId, Integer userId) {
        Film film = findFilmOrElseThrow(filmId);
        User user = findUserOrElseThrow(userId);
        filmStorage.deleteLike(filmId, userId);
        feedDao.addFeed(userId, Event.LIKE, Operation.REMOVE, filmId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        if (count < 0) {
            log.warn("Невалидное значение параметра count");
            throw new InvalidParameter(String.format("Невалидное значение count: %d", count));
        }

        Map<String, Integer> params = new LinkedHashMap<>();
        params.put("genreId", genreId);
        params.put("year", year);
        params.put("count", count);

        return filmStorage.getTopFilms(params);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        User user = findUserOrElseThrow(userId);
        User friend = findUserOrElseThrow(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private Film findFilmOrElseThrow(Integer filmId) {
        return filmStorage.findById(filmId).orElseThrow(
                () -> new FilmNotFoundException(filmId)
        );
    }

    private User findUserOrElseThrow(Integer userId) {
        return userStorage.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
    }

    private Director findDirectorOrElseThrow(Integer directorId) {
        return directorDao.findById(directorId).orElseThrow(
                () -> new DirectorNotFoundException(directorId)
        );
    }

    public List<Film> findFilmsBySearch(String query, List<String> by) {
        return filmStorage.findFilmsBySearch(query, by);
    }
}
