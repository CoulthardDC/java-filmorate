package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Digits;
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
    @Digits(integer = 2_147_483_647, fraction = 0)
    @NotNull(message = "Пользователь отсутствует.")
    private int userId;
    @Digits(integer = 2_147_483_647, fraction = 0)
    @NotNull(message = "Фильм отсутствует.")
    private int filmId;
    private Integer useful;
    private Map<Boolean, Set<Integer>> likes;

    public Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.content);
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());
        return values;
    }
}