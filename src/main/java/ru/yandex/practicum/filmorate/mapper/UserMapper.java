package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserMapper {

    public static User mapToUser(ResultSet rs, int rowNum) throws SQLException{
        Integer userId = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String name = rs.getString("name");
        User user = new User(email, login, birthday);

        user.setName(name);
        user.setId(userId);
        return user;
    }
}
