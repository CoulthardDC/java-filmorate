package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper {
    public static Director mapToDirector(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
