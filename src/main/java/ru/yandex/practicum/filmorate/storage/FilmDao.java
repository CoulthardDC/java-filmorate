package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmDao {
    Integer count();

    void deleteAll();

    void deleteById(Integer id);

    List<Film> findAll();

    Optional<Film> findById(Integer id);

    Film save(Film film);

    Optional<List<Integer>> findLikesByFilmId(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getTopFilms(Map<String, Integer> params);

    List<Film> findFilmsByDirectorId(Integer directorId, String sortBy);

    List<Film> findFilmsBySearch(String query, List<String> by);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
