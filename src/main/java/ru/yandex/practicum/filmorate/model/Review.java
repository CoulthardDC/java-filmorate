package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class Review {
    private int reviewId;
    @NotNull(message = "Текст отзыва отсутствует.")
    private String content;
    @NotNull(message = "Тип отзыва отсутствует.")
    private Boolean isPositive;
    @NotNull(message = "Пользователь отсутствует.")
    private int userId;
    @NotNull(message = "Фильм отсутствует.")
    private int filmId;
    private Integer useful;
    private Map<Boolean, Set<Integer>> likes;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful == null ? 0 : useful);
        return values;
    }
}