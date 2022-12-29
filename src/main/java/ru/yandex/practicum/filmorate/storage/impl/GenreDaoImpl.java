package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;
import java.util.Optional;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sqlRequest = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlRequest, GenreMapper::mapToGenre);
    }

    @Override
    public Optional<Genre> findById(Integer genreId) {
        String sqlRequest = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sqlRequest, GenreMapper::mapToGenre, genreId)
                .stream()
                .findFirst();
    }
}
