package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FeedDao;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import ru.yandex.practicum.filmorate.storage.UserDao;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final DirectorDao directorDao;
    private final FeedDao feedDao;

    @Autowired
    public FilmService(@Qualifier("filmDbDaoImpl") FilmDao filmDao,
                       @Qualifier("userDbDaoImpl") UserDao userDao,
                       DirectorDao directorDao,
                       FeedDao feedDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
    }

    public List<Film> getAllFilms() {
        return filmDao.findAll();
    }

    public Film getFilmById(Integer id) {
        return findFilmOrElseThrow(id);
    }

    public void addFilm(Film film) {
        filmDao.save(film);
    }

    public void updateFilmById(Film film) {
        filmDao.save(film);
    }

    public List<Film> findFilmsByDirectorId(Integer directorId, String sortBy) {
        findDirectorOrElseThrow(directorId);
        if (sortBy.equals("year") || sortBy.equals("likes")) {
            return filmDao.findFilmsByDirectorId(directorId, sortBy);
        } else {
            throw new InvalidParameter(String.format("Невалидное значение sortBy: %s", sortBy));
        }
    }

    public void removeFilmById(Integer id) {
        filmDao.deleteById(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        findFilmOrElseThrow(filmId);
        findUserOrElseThrow(userId);
        filmDao.addLike(filmId, userId);
        feedDao.addFeed(userId, Event.LIKE, Operation.ADD, filmId);
    }

    public void removeLikeFromFilm(Integer filmId, Integer userId) {
        findFilmOrElseThrow(filmId);
        findUserOrElseThrow(userId);
        filmDao.deleteLike(filmId, userId);
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

        return filmDao.getTopFilms(params);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        findUserOrElseThrow(userId);
        findUserOrElseThrow(friendId);
        return filmDao.getCommonFilms(userId, friendId);
    }

    private Film findFilmOrElseThrow(Integer filmId) {
        return filmDao.findById(filmId).orElseThrow(
                () -> new FilmNotFoundException(filmId)
        );
    }

    private void findUserOrElseThrow(Integer userId) {
        userDao.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
    }

    private void findDirectorOrElseThrow(Integer directorId) {
        directorDao.findById(directorId).orElseThrow(
                () -> new DirectorNotFoundException(directorId)
        );
    }

    public List<Film> findFilmsBySearch(String query, List<String> by) {
        String parameterByTitle = "title";
        String parameterByDirector = "director";

        if (query.isEmpty()) {
            throw new ValidationException("Отсутсвует параметр строки запроса query.");
        } else if (by.isEmpty()) {
            throw new ValidationException("Отсутсвует параметр строки запроса by.");
        } else if (by.size() == 1 && (!by.get(0).equals(parameterByTitle) && !by.get(0).equals(parameterByDirector))) {
            throw new ValidationException(String.format("Неправильный параметр строки запроса by = %s.", by.get(0)));
        } else if ((by.size() == 2) && ((!by.get(0).equals(parameterByTitle) && !by.get(0).equals(parameterByDirector))
                && (!by.get(1).equals(parameterByTitle) && !by.get(1).equals(parameterByDirector)))) {
            throw new ValidationException(String.format("Неправильные параметры строки запроса by = %s, %s.", by.get(0),
                    by.get(1)));
        }
        return filmDao.findFilmsBySearch(query, by);
    }
}
