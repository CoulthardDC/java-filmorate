package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDao {

    List<Mpa> findAll();
    Optional<Mpa> findById(Integer id);
}
