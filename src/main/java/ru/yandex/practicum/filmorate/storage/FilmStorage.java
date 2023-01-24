package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Integer count();
    void deleteAll();
    void deleteById(Integer id);
    List<Film> findAll();
    Optional<Film> findById(Integer id);
    Film save(Film film);
    Optional<List<Integer>> findLikesByFilmId(Integer filmId);
    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
    List<Film> getTopFilms(String sqlIn, List<Integer> params);
    List<Film> findFilmsByDirectorId(Integer directorId, String sortBy);
}
