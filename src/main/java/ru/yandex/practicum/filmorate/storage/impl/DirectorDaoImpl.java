package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors;";
        return jdbcTemplate.query(sql, DirectorMapper::mapToDirector);
    }

    @Override
    public Optional<Director> findById(Integer id) {
        String sql = "SELECT * FROM directors d WHERE d.id = ?;";
        List<Director> result = jdbcTemplate.query(sql, DirectorMapper::mapToDirector, id);
        return result.stream()
                .findFirst();
    }

    @Override
    public Director update(Director director) {
        String sqlRequest = "UPDATE directors SET name = ?" +
                    " WHERE id = ?";
        int resultCount = jdbcTemplate.update(sqlRequest, director.getName(),
                    director.getId());
        if (resultCount < 1) {
            throw new DirectorNotFoundException(director.getId());
        }
        return director;
    }

    @Override
    public Director create(Director director) {
        String sqlRequest = "INSERT INTO directors (name)"
                + " VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlRequest,
                    new String[]{"id"}
            );
            stmt.setString(1, director.getName());
            return stmt;
            }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public void deleteById(Integer id) {
        String sqlRequest = "DELETE FROM directors WHERE id = ?";
        if (jdbcTemplate.update(sqlRequest, id) < 1) {
            throw new DirectorNotFoundException(id);
        }
    }
}
