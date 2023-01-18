package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InvalidParameter;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDao directorDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorDao = directorDao;
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
    }

    public void removeLikeFromFilm(Integer filmId, Integer userId) {
        Film film = findFilmOrElseThrow(filmId);
        User user = findUserOrElseThrow(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count < 0) {
            log.warn("Невалидное значение параметра count");
            throw new InvalidParameter(String.format("Невалидное значение count: %d", count));
        } else {
            return filmStorage.getTopFilms(count);
        }
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
}
