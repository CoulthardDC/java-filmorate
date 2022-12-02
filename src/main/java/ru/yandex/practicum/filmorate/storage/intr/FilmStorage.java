package ru.yandex.practicum.filmorate.storage.intr;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    Film removeFilmById(Integer id);
    Film updateFilmById(Integer id, Film film);
    Film getFilmById(Integer id);
    List<Film> getAllFilms();
}
