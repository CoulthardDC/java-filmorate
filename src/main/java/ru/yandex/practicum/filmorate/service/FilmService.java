package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InvalidParameterCount;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
            throw new InvalidParameterCount(String.format("Невалидное значение count: %d", count));
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
}
