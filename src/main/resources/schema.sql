CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    name VARCHAR(50),
    BIRTHDAY DATE,
    CONSTRAINT user_pk PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    from_user_id INT,
    to_user_id INT,
    status BOOLEAN,
    CONSTRAINT friendship_pk PRIMARY KEY (from_user_id, to_user_id),
    CONSTRAINT from_fk FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT to_fk FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INT,
    name VARCHAR(50),
    CONSTRAINT genre_pk PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INT,
    name VARCHAR(50),
    CONSTRAINT mpa_pk PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS films (
    film_id INT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50),
    release_date DATE,
    duration LONG,
    mpa_id INT,
    CONSTRAINT film_pk PRIMARY KEY (film_id),
    CONSTRAINT mpa_fk FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);


CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    CONSTRAINT film_genre_pk PRIMARY KEY (film_id, genre_id),
    CONSTRAINT film_fk FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INT,
    user_id INT,
    CONSTRAINT like_pk PRIMARY KEY (film_id, user_id),
    CONSTRAINT film_like_fk FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT user_like_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
    id INT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT directors_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id INT,
    director_id INT,
    CONSTRAINT film_director_pk PRIMARY KEY (film_id, director_id),
    CONSTRAINT films_fk FOREIGN KEY (film_id) REFERENCES films (film_id),
    CONSTRAINT directors_fk FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER REFERENCES users (user_id) ON DELETE RESTRICT,
    film_id INTEGER REFERENCES films (film_id) ON DELETE RESTRICT,
    useful INTEGER
);

CREATE TABLE IF NOT EXISTS reviews_likes_dislikes (
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE RESTRICT,
    user_id INTEGER REFERENCES users (user_id) ON DELETE RESTRICT,
    is_like BOOLEAN,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS feeds (
    event_id INTEGER AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    event_type VARCHAR(50) NOT NULL,
    operation VARCHAR(50) NOT NULL,
    entity_id INTEGER,
    CONSTRAINT feeds_pk PRIMARY KEY(event_id),
    CONSTRAINT feeds_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW films_mpa_view AS
SELECT f.film_id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       f.mpa_id,
       m.name AS mpa_name
FROM films f
LEFT OUTER JOIN mpa m ON f.mpa_id = m.mpa_id;

CREATE OR REPLACE VIEW film_director_directors_view AS
SELECT fd.film_id,
       fd.director_id,
       d.name AS director
FROM film_director fd
LEFT OUTER JOIN directors d ON fd.director_id = d.id;
