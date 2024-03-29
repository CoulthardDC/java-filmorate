package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.dto.error.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class,
            UserNotFoundException.class,
            GenreNotFoundException.class,
            MpaNotFoundedException.class}
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final InvalidIdException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ресурс не найден", e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidIdException(final InvalidIdException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ресурс не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInvalidParameterCount(final InvalidParameterCount e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка при выполнении программы", e.getMessage());
    }
}
