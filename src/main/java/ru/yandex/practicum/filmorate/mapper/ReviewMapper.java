package ru.yandex.practicum.filmorate.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@AllArgsConstructor
public class ReviewMapper {
    private final JdbcTemplate jdbcTemplate;
}
