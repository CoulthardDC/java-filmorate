package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
public class User {
    Integer id;
    @NonNull
    @Email
    String email;
    @NonNull
    String login;
    String name;
    LocalDate birthday;
}
