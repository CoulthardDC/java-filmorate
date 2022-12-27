package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.List;
import java.util.Optional;

@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sqlRequest = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlRequest, MpaMapper::mapToMpa);
    }

    @Override
    public Optional<Mpa> findById(Integer mpaId) {
        String sqlRequest = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.query(sqlRequest, MpaMapper::mapToMpa, mpaId)
                .stream()
                .findFirst();
    }
}
