package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundedException extends InvalidIdException {
    public MpaNotFoundedException(Integer id) {
        super(String.format("Mpa с id = %d не найден", id));
    }
}
