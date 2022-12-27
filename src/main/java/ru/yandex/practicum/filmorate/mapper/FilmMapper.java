package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper {
    public static Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration")
        );
        film.setId(rs.getInt("film_id"));
        film.getMpa().setId(rs.getInt("mpa_id"));
        film.getMpa().setName(rs.getString("mpa_name"));
        return film;
    }
}
