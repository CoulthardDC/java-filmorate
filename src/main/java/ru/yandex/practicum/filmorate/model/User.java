package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.LoginConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    @NonNull
    @Email
    String email;
    @LoginConstraint
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
    Set<Integer> friendsIds = new HashSet<>();


    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = login;
    }

    public boolean addFriend(Integer id) {
        return friendsIds.add(id);
    }

    public boolean removeFriend(Integer id) {
        return friendsIds.remove(id);
    }
}
