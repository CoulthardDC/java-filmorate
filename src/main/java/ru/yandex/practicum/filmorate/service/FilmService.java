package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.exception.InvalidParameterCount;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        return findFilmOrElseThrow(id);
    }

    public Film addFilm(Film film) {
        filmStorage.save(film);
        return film;
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
        film.addLike(userId);
    }

    public void removeLikeFromFilm(Integer filmId, Integer userId) {
        Film film = findFilmOrElseThrow(filmId);
        if (!film.removeLike(userId)) {
            throw new InvalidIdException(String.format(
                    "Юзер id = %d не ставил лайк фильму id = %d", userId, filmId));
        }
    }

    public List<Film> getTopFilms(Integer count) {
        if (count < 0) {
            log.warn("Невалидное значение параметра count");
            throw new InvalidParameterCount(String.format("Невалидное значение count: %d", count));
        } else {
            Comparator<Film> comparator = Comparator.comparing(o -> o.getLikes().size());

            return filmStorage.findAll()
                    .stream()
                    .sorted(comparator.reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private Film findFilmOrElseThrow(Integer filmId) {
        return filmStorage.findById(filmId).orElseThrow(
                () -> new FilmNotFoundException(filmId));
    }
}
