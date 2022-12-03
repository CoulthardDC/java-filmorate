package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Invalid name")
    public void invalidNameShouldFailValidation() {
        Film film = new Film(
                " ",
                "пустое название фильма",
                LocalDate.of(2000, Month.JULY, 20),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid name")
    public void validNameShouldPassValidation() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(1985, Month.JULY, 3),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid size")
    public void invalidDescriptionSizeShouldFailValidation() {
        Film film = new Film(
                "Назад в будущее",
                new String(new char[201]).replace('\0', ' '),
                LocalDate.of(2000, Month.JULY, 20),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid size")
    public void validDescriptionSizeShouldPassValidation() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(1985, Month.JULY, 3),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid release date")
    public void invalidReleaseDateShouldFailValidation() {
        Film film = new Film(
                "Несуществующий фильм",
                "Описание несуществующего фильма",
                LocalDate.of(1700, Month.JULY, 21),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid release date")
    public void validReleaseDateShouldPassValidation() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(1985, Month.JULY, 3),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid duration")
    public void invalidDurationShouldFailValidation() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(2000, Month.JULY, 20),
                (long)-100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid duration")
    public void validDurationShouldPassValidation() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(1985, Month.JULY, 3),
                (long)100
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}
