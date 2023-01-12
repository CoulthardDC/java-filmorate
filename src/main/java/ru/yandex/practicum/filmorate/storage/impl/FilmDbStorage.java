package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer count() {
        String sqlRequest = "SELECT count(*) FROM films";
        return jdbcTemplate.queryForObject(sqlRequest, Integer.class);
    }

    @Override
    public void deleteAll() {
        String sqlRequest = "DELETE FROM films";
        jdbcTemplate.update(sqlRequest);
    }

    @Override
    public void deleteById(Integer filmId) {
        String sqlRequest = "DELETE FROM films" +
                " WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest, filmId);
    }

    @Override
    public List<Film> findAll() {
        String sqlRequest = "SELECT f.*, m.name as mpa_name"
                +" FROM films f"
                +" LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm);
        setGenresAndLikes(films);
        return films;
    }

    @Override
    public Optional<Film> findById(Integer filmId) {
        String sqlRequest = "SELECT f.*, m.name as mpa_name"
                +" FROM films AS f"
                +" LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id"
                +" WHERE film_id = ?";
        List<Film> result = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm, filmId);
        setGenresAndLikes(result);
        return result
                .stream()
                .findFirst();
    }

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            String sqlRequest = "INSERT INTO films (name, description, release_date, duration, mpa_id)"
                    + " VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlRequest,
                        new String[]{"film_id"}
                );
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4,film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().intValue());
        } else {
            if(isFilm(film.getId())) {
                String sqlRequest = "UPDATE films SET name = ?, description = ?," +
                        " release_date = ?, duration = ?, mpa_id = ?" +
                        " WHERE film_id = ?";
                jdbcTemplate.update(sqlRequest, film.getName(),
                        film.getDescription(),
                        Date.valueOf(film.getReleaseDate()),
                        film.getDuration(),
                        film.getMpa().getId(),
                        film.getId());
            }
        }
        String mpaRequest = "SELECT m.* FROM films f "
                + "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id "
                + "WHERE film_id = ?";
        Mpa mpa = jdbcTemplate.query(mpaRequest, MpaMapper::mapToMpa, film.getId())
                .stream()
                .findFirst()
                .get();
        film.setMpa(mpa);
        updateGenres(film);
        setGenresAndLikes(List.of(film));
        return film;
    }

    @Override
    public Optional<List<Integer>> findLikesByFilmId(Integer filmId) {
        if(isFilm(filmId)) {
            String sqlRequest = "SELECT user_id FROM likes WHERE film_id = ?";
            List<Integer> likes = jdbcTemplate.queryForList(sqlRequest, Integer.class, filmId);
            return Optional.of(likes);
        }
        return Optional.empty();
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        if(isFilm(filmId)) {
            String sqlRequest = "merge into likes(film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlRequest, filmId, userId);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        if(isFilm(filmId)) {
            String sqlRequest = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlRequest, filmId, userId);
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sqlRequest = "SELECT f.film_id, f.name, f.description, f.release_date, " +
                "f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY (f.film_id)" +
                "ORDER BY (count(l.user_id)) DESC, f.film_id " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm, count);
        setGenresAndLikes(result);
        return result;
    }


    private void updateGenres(Film film) {
        String sqlRequest = "DELETE from film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest, film.getId());
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            sqlRequest =  "merge into film_genres(film_id, genre_id) \n" +
                    "values (?, ?)";
            jdbcTemplate.batchUpdate(sqlRequest, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genres.get(i).getId());
                }
                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    private boolean isFilm(Integer filmId) {
        String sqlRequest = "SELECT count(*) FROM films WHERE film_id = ?";
        Integer result = jdbcTemplate.queryForObject(sqlRequest, Integer.class, filmId);
        return result != 0;
    }

    private void setGenresAndLikes(List<Film> films) {
        List<Integer> filmsId = films
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        if (filmsId.isEmpty()) {
            return;
        }
        films.forEach(film -> film.getGenres().clear());
        Map<Integer, Film> filmMap = new HashMap<>();

        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
        String sqlRequest = "SELECT fg.film_id, g.* " +
                "FROM film_genres AS fg " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id in (%s) " +
                "ORDER BY fg.genre_id";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs -> {
                    Integer filmId = rs.getInt("film_id");
                    Integer genreId =  rs.getInt("genre_id");
                    String genreName = rs.getString("name");
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(genreName);
                    filmMap.get(filmId).addGenre(genre);
                },
                filmsId.toArray());

        sqlRequest = "SELECT * FROM likes WHERE film_id in (%s)";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs-> {
            filmMap.get(rs.getInt("film_id"))
                    .addLike(rs.getInt("user_id"));
        }, filmsId.toArray());
    }
}
