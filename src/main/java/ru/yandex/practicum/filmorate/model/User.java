package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.LoginConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    @NonNull
    @Email
    final String email;
    @LoginConstraint
    final String login;
    String name;
    @PastOrPresent
    final LocalDate birthday;


    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;

        this.name = login;
    }
}
