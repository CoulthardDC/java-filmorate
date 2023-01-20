package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
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
                + " FROM films f"
                + " LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm);
        setAll(films);
        return films;
    }

    @Override
    public Optional<Film> findById(Integer filmId) {
        String sqlRequest = "SELECT f.*, m.name as mpa_name"
                + " FROM films AS f"
                + " LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id"
                + " WHERE film_id = ?";
        List<Film> result = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm, filmId);
        setAll(result);
        return result
                .stream()
                .findFirst();
    }

    public List<Film> findFilmsByDirectorId(Integer directorId, String sortBy) {
        List<Film> result = new ArrayList<>();
        if (sortBy.equals("year")) {
            String sql = "SELECT f.*, m.name as mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_director fd ON fd.film_id=f.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date;";
            result = jdbcTemplate.query(sql, FilmMapper::mapToFilm, directorId);
        } else if (sortBy.equals("likes")) {
            String sql = "SELECT f.*, m.name as mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_director fd ON fd.film_id=f.film_id " +
                    "LEFT JOIN likes l ON l.film_id=f.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC;";
            result = jdbcTemplate.query(sql, FilmMapper::mapToFilm, directorId);
        }
        setAll(result);
        return result;
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
                stmt.setLong(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().intValue());
        } else {
            if (isFilm(film.getId())) {
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
        updateDirectors(film);
        setAll(List.of(film));
        return film;
    }

    @Override
    public Optional<List<Integer>> findLikesByFilmId(Integer filmId) {
        if (isFilm(filmId)) {
            String sqlRequest = "SELECT user_id FROM likes WHERE film_id = ?";
            List<Integer> likes = jdbcTemplate.queryForList(sqlRequest, Integer.class, filmId);
            return Optional.of(likes);
        }
        return Optional.empty();
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        if (isFilm(filmId)) {
            String sqlRequest = "merge into likes(film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlRequest, filmId, userId);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        if (isFilm(filmId)) {
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
        setAll(result);
        return result;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sqlRequest =  "SELECT f.film_id, f.name, f.description, f.release_date, " +
                "f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "INNER JOIN likes l ON f.film_id = l.film_id " +
                "WHERE l.user_id = ? AND ? " +
                "GROUP BY (f.film_id) " +
                "ORDER BY (count(l.user_id)) DESC, f.film_id";
        List<Film> result = jdbcTemplate.query(sqlRequest, FilmMapper::mapToFilm, userId, friendId);
        setAll(result);
        return result;
    }

    private void updateGenres(Film film) {
        String sqlRequest = "DELETE from film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest, film.getId());
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            sqlRequest = "merge into film_genres(film_id, genre_id) \n" +
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

    private void updateDirectors(Film film) {
        String sqlRequest = "DELETE from film_director WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest, film.getId());
        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            sqlRequest = "merge into film_director(film_id, director_id) \n" +
                    "values (?, ?)";
            jdbcTemplate.batchUpdate(sqlRequest, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    private boolean isFilm(Integer filmId) {
        String sqlRequest = "SELECT count(*) FROM films WHERE film_id = ?";
        Integer result = jdbcTemplate.queryForObject(sqlRequest, Integer.class, filmId);
        return result != null && result != 0;
    }

    private void setAll(List<Film> films) {
        List<Integer> filmsId = films
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        if (filmsId.isEmpty()) {
            return;
        }
        films.forEach(film -> film.getGenres().clear());
        films.forEach(film -> film.getDirectors().clear());
        Map<Integer, Film> filmMap = new HashMap<>();

        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }

        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));

        setGenres(filmsId, filmMap, inSql);
        setLikes(filmsId, filmMap, inSql);
        setDirectors(filmsId, filmMap, inSql);
    }

    private void setGenres(List<Integer> filmsId, Map<Integer, Film> filmMap, String inSql) {
        String sqlRequest = "SELECT fg.film_id, g.* " +
                "FROM film_genres AS fg " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id in (%s) " +
                "ORDER BY fg.genre_id";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs -> {
                    Integer filmId = rs.getInt("film_id");
                    Integer genreId = rs.getInt("genre_id");
                    String genreName = rs.getString("name");
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(genreName);
                    filmMap.get(filmId).addGenre(genre);
                },
                filmsId.toArray());
    }

    private void setLikes(List<Integer> filmsId, Map<Integer, Film> filmMap, String inSql) {
        String sqlRequest = "SELECT * FROM likes WHERE film_id in (%s)";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs -> {
            filmMap.get(rs.getInt("film_id"))
                    .addLike(rs.getInt("user_id"));
        }, filmsId.toArray());
    }

    private void setDirectors(List<Integer> filmsId, Map<Integer, Film> filmMap, String inSql) {
        String sqlRequest = "SELECT fd.film_id, d.* " +
                "FROM film_director AS fd " +
                "LEFT JOIN directors AS d ON fd.director_id = d.id " +
                "WHERE fd.film_id in (%s) " +
                "ORDER BY fd.director_id";
        jdbcTemplate.query(String.format(sqlRequest, inSql), rs -> {
                    Integer filmId = rs.getInt("film_id");
                    Integer directorId = rs.getInt("id");
                    String directorName = rs.getString("name");
                    Director director = new Director();
                    director.setId(directorId);
                    director.setName(directorName);
                    filmMap.get(filmId).addDirector(director);
                },
                filmsId.toArray());
    }
}
