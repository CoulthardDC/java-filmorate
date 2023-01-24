package ru.yandex.practicum.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LikeExtractor implements ResultSetExtractor<Map<Integer, Set<Integer>>> {
    @Override
    public Map<Integer, Set<Integer>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Set<Integer>> userAndFilmIds = new HashMap<>();
        while (rs.next()) {
            Integer userId = rs.getInt("user_id");
            Integer filmId = rs.getInt("film_id");
            if (!userAndFilmIds.containsKey(userId)) {
                Set<Integer> filmIds = new HashSet<>();
                filmIds.add(filmId);
                userAndFilmIds.put(userId, filmIds);
            } else {
                userAndFilmIds.get(userId).add(filmId);
            }
        }
        return userAndFilmIds;
    }
}
