CREATE TABLE authors
(
    author_id  SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    birth_date DATE,
    country    VARCHAR(50)
);

CREATE INDEX idx_lower_name ON public.authors(LOWER(name));

CREATE TABLE books
(
    book_id          SERIAL PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    publication_year INT,
    author_id        INT REFERENCES authors (author_id) ON DELETE CASCADE
);

INSERT INTO authors (name, birth_date, country)
VALUES ('Лев Толстой', '1828-09-09', 'Россия'),
       ('Фёдор Достоевский', '1821-11-11', 'Россия'),
       ('Эрнест Хемингуэй', '1899-07-21', 'США');

INSERT INTO books (title, publication_year, author_id)
VALUES ('Война и мир', 1869, 1),
       ('Анна Каренина', 1877, 1),
       ('Преступление и наказание', 1866, 2),
       ('Идиот', 1869, 2),
       ('Старик и море', 1952, 3),
       ('Прощай, оружие!', 1929, 3);