package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends InvalidIdException {
    public GenreNotFoundException(Integer id) {
        super(String.format("Жанр с id = %d не найден", id));
    }
}
