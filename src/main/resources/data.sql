merge into genres(GENRE_ID, genre_name)
    values (1, 'Комедия');

merge into genres(GENRE_ID, genre_name)
    values (2, 'Драма');

merge into genres(GENRE_ID, genre_name)
    values (3, 'Мультфильм');

merge into genres(GENRE_ID, genre_name)
    values (4, 'Триллер');

merge into genres(GENRE_ID, genre_name)
    values (5, 'Документальный');

merge into genres(GENRE_ID, genre_name)
    values (6, 'Боевик');


merge into mpa(MPA_ID, NAME)
    VALUES (1, 'G');
merge into mpa(MPA_ID, NAME)
    VALUES (2, 'PG');
merge into mpa(MPA_ID, NAME)
    VALUES (3, 'PG-13');
merge into mpa(MPA_ID, NAME)
    VALUES (4, 'R');
merge into mpa(MPA_ID, NAME)
    VALUES (5, 'NC-17');