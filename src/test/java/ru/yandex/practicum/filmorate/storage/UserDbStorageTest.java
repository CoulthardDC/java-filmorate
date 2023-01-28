package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbDaoImpl;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-films-users.sql"})
class UserDbStorageTest {
    private final UserDbDaoImpl userStorage;

    @Test
    void testFindById() {
        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void testFindAllUsers() {
        Optional<List<User>> usersList = Optional.ofNullable(userStorage.findAll());

        assertThat(usersList)
                .isPresent();

        assertEquals(3, usersList.get().size());
    }

    @Test
    void testCreateUserWithoutName() {
        User user = new User(
                "evgen@yandex.ru",
                "evgen",
                LocalDate.of(1998, Month.JUNE, 28)
        );
        userStorage.save(user);
        user.setId(4);

        assertEquals(user, userStorage.findById(4).get());
    }

    @Test
    void testUpdateUser() {
        Optional<User> userToUpdate = userStorage.findById(1);

        assertThat(userToUpdate)
                .isPresent();
        userToUpdate.get().setEmail("updatedMail@mail.ru");

        userStorage.save(userToUpdate.get());

        assertThat(userToUpdate)
                .contains(userStorage.findById(1).get());
    }

    @Test
    void testAddFriend() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);

        assertThat(userStorage.findFriendsByUserId(1).get())
                .hasSize(1);
        assertThat(userStorage.findFriendsByUserId(2).get())
                .hasSize(1);
    }

    @Test
    void testGetMutualFriend() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);

        assertThat(userStorage.findFriendsByUserId(1).get())
                .isEqualTo(userStorage.findFriendsByUserId(3).get());

        assertThat(userStorage.getCommonFriends(1, 3))
                .contains(userStorage.findById(2).get());
    }

    @Test
    void testDeleteFriends() {
        userStorage.addFriend(1, 2);

        assertThat(userStorage.findFriendsByUserId(1).get())
                .isNotEmpty();

        userStorage.deleteFriend(1, 2);
        assertThat(userStorage.findFriendsByUserId(1).get())
                .isEmpty();
    }
}