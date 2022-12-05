package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Invalid email")
    public void invalidEmailShouldFailValidation() {
        User user = new User(
                "ev gen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid email")
    public void validEmailShouldPassValidation() {
        User user = new User(
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid login")
    public void invalidLoginShouldFailValidation() {
        User userOne = new User(
                "evgen@yandex.ru",
                " ",
                LocalDate.of(1998, Month.JUNE, 28)
        );

        User userTwo = new User(
                "evgen@yandex.ru",
                "ev gen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violationsOne = validator.validate(userOne);
        Set<ConstraintViolation<User>> violationsTwo = validator.validate(userTwo);
        assertAll(
                () -> assertFalse(violationsOne.isEmpty()),
                () -> assertFalse((violationsTwo.isEmpty()))
        );
    }

    @Test
    @DisplayName("Valid login")
    public void validLoginShouldPassValidation() {
        User user = new User (
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Login without name")
    public void nameShouldBeLikeLogin() {
        User userOne = new User (
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );

        User userTwo = new User (
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        userTwo.setName("evgen");
        assertEquals(userTwo, userOne);
    }

    @Test
    @DisplayName("Invalid date")
    public void invalidBirthdayShouldFailValidation() {
        User user = new User (
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(2077, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid date")
    public void validBirthdayPassFailValidation() {
        User user = new User (
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}