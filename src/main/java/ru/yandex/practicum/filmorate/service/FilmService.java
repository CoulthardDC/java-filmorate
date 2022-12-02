package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.exception.InvalidParameterCount;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.intr.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Фльм с id = {} не найден", id);
            throw new InvalidIdException(String.format("Фильм с id = %d не найден", id));
        } else {
            return film;
        }
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilmById(Film film) {
        if (filmStorage.updateFilmById(film.getId(), film) == null) {
            log.warn("Фльим с id = {} не обновлен, т.к. не найден", film.getId());
            throw new InvalidIdException(String.format("Фильм с id = %d не обновлен, т.к. не найден", film.getId()));
        } else {
            return film;
        }
    }

    public Film removeFilmById(Integer id) {
        Film removedFilm = filmStorage.removeFilmById(id);
        if (removedFilm == null) {
            log.warn("Фильм с id = {} не удален, т.к. не найден", id);
            throw new InvalidIdException(String.format("Фильм с id = %d не удален, т.к. не найден", id));
        } else {
            return removedFilm;
        }
    }

    public boolean addLikeToFilm(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.warn("Фильм с id = {} не найден" ,filmId);
            throw new InvalidIdException(String.format("Фильм с id = %d не найден", filmId));
        } else {
            return filmStorage.getFilmById(filmId).addLike(userId);
        }
    }

    public boolean removeLikeFromFilm(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.warn("Фильм с id = {} не найден" ,filmId);
            throw new InvalidIdException(String.format("Фильм с id = %d не найден", filmId));
        } else {
            boolean isLiked = filmStorage.getFilmById(filmId).removeLike(userId);
            if (!isLiked) {
                throw new InvalidIdException(String.format("Пользователь с id = %d не найден", userId));
            }
            return isLiked;
        }
    }

    public List<Film> getTopFilms(Integer count) {
        if (count < 0) {
            log.warn("Невалидное значение параметра count");
            throw new InvalidParameterCount(String.format("Невалидное значение count: %d", count));
        } else {
            Comparator<Film> comparator = Comparator.comparing(o -> o.getLikersIds().size());

            return filmStorage.getAllFilms()
                    .stream()
                    .sorted(comparator.reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }
}
