package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbDaoImpl;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-films-users.sql"})
class FilmDbStorageTest {
    private final FilmDbDaoImpl filmStorage;

    @Test
    void testFindById() {
        Optional<Film> filmOptional = filmStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void testFindAllFilms() {
        Optional<List<Film>> filmList = Optional.ofNullable(filmStorage.findAll());

        assertThat(filmList)
                .isPresent();

        assertEquals(3, filmList.get().size());
    }


    @Test
    void testCreateFilm() {
        Film film = new Film(
                "Назад в будущее",
                "Хороший фильм",
                LocalDate.of(1985, Month.JULY, 3),
                (long)100
        );
        film.getMpa().setId(3);

        filmStorage.save(film);
        film.setId(4);
        film.getMpa().setName("PG-13");

        assertThat(film)
                .isEqualTo(filmStorage.findById(4).get());
    }

    @Test
    void testUpdateFilm() {
        Optional<Film> filmToUpdate = filmStorage.findById(2);

        assertThat(filmToUpdate)
                .isPresent();

        filmStorage.save(filmToUpdate.get());

        assertThat(filmToUpdate)
                .contains(filmStorage.findById(2).get());
    }

    @Test
    void testAddLike() {
        filmStorage.addLike(1, 2);

        assertThat(filmStorage.findById(1).get())
                .hasFieldOrPropertyWithValue("likes", Set.of(2));
    }

    @Test
    void testDeleteLike() {
        filmStorage.addLike(1, 2);

        assertThat(filmStorage.findById(1).get())
                .hasFieldOrPropertyWithValue("likes", Set.of(2));

        filmStorage.deleteLike(1, 2);
        assertThat(filmStorage.findById(1).get())
                .hasFieldOrPropertyWithValue("likes", Set.of());
    }

    @Test
    void deleteAll() {
        filmStorage.deleteAll();

        assertThat(filmStorage.findAll())
                .isEmpty();
    }
}