package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends InvalidIdException {
    public DirectorNotFoundException(Integer id) {
        super(String.format("Режиссёр с id = %d не найден", id));
    }
}
