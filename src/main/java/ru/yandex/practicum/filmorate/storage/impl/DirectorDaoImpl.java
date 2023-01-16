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
        if (isDirector(director.getId())) {
            String sqlRequest = "UPDATE directors SET name = ?" +
                    " WHERE id = ?";
            jdbcTemplate.update(sqlRequest, director.getName(),
                    director.getId());
            return director;
        } else {
            throw new DirectorNotFoundException(director.getId());
        }

    }

    @Override
    public Director create(Director director) {
        if (director.getId() == null || !isDirector(director.getId())) {
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
        } else {
            throw new DirectorNotFoundException(director.getId());
        }
    }


    @Override
    public void deleteById(Integer id) {
        if (isDirector(id)) {
            String sqlRequest = "DELETE FROM directors WHERE id = ?";
            jdbcTemplate.update(sqlRequest, id);
        }
    }

    private boolean isDirector(Integer id) {
        String sqlRequest = "SELECT count(*) FROM directors WHERE id = ?";
        Integer result = jdbcTemplate.queryForObject(sqlRequest, Integer.class, id);
        return result != null && result != 0;
    }
}
