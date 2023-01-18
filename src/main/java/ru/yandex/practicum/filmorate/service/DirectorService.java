package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorDao directorDao;

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public List<Director> getAll() {
        return directorDao.findAll();
    }

    public Director getDirectorById(Integer id) {
        return findDirectorOrElseThrow(id);
    }

    public Director createDirector(Director director) {
        return directorDao.create(director);
    }

    public Director updateDirector(Director director) {
        findDirectorOrElseThrow(director.getId());
        return directorDao.update(director);
    }

    public void deleteDirectorById(Integer id) {
        findDirectorOrElseThrow(id);
        directorDao.deleteById(id);
    }

    private Director findDirectorOrElseThrow(Integer id) {
        return directorDao.findById(id).orElseThrow(
                () -> new DirectorNotFoundException(id)
        );
    }
}