package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @Override
    public Integer count() {
        return id;
    }

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public void deleteById(Integer id) {
        films.remove(id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            film.setId(++id);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<List<Integer>> findLikesByFilmId(Integer filmId) {
        if (findById(filmId).isPresent()) {
            return Optional.of(new ArrayList<>(films.get(filmId).getLikes()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {

    }

    @Override
    public List<Film> getTopFilms(String sqlIn, List<Integer> params) {
        return null;
    }

    @Override
    public List<Film> findFilmsByDirectorId(Integer directorId, String sortBy) {
        return null;
    }

    @Override
    public List<Film> findFilmsBySearch(String query, List<String> by) {
        return null;
    }
    
    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return null;
    }
}
