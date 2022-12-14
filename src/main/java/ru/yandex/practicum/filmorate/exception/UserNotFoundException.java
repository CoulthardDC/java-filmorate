package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends InvalidIdException{
    public UserNotFoundException(Integer id) {
        super(String.format("Пользователь с id = %d не найден", id));
    }
}
