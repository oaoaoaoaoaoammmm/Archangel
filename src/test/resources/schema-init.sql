CREATE TABLE authors
(
    author_id  SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    birth_date DATE,
    country    VARCHAR(50)
);

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
       ('Антон Чехов', '1860-01-29', 'Россия'),
       ('Александр Пушкин', '1799-06-06', 'Россия'),
       ('Иван Тургенев', '1818-11-09', 'Россия'),
       ('Николай Гоголь', '1809-04-01', 'Россия'),
       ('Михаил Булгаков', '1891-05-15', 'Россия'),
       ('Александр Солженицын', '1918-12-11', 'Россия'),
       ('Эрнест Хемингуэй', '1899-07-21', 'США'),
       ('Марк Твен', '1835-11-30', 'США');

DO
$$
    DECLARE
        i                 INT;
        current_author_id INT;
        book_title        TEXT;
        author_count      INT    := 10;
        special_authors   INT[]  := ARRAY [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
        special_books     TEXT[] := ARRAY [
            'Война и мир', 'Преступление и наказание', 'Вишневый сад', 'Евгений Онегин', 'Отцы и дети',
            'Мёртвые души', 'Мастер и Маргарита', 'Один день Ивана Денисовича', 'Старик и море', 'Приключения Тома Сойера'
            ];
    BEGIN
        FOR i IN 1..100000
            LOOP
                IF (i - 1) % 10000 = 0 AND (i - 1) / 10000 < array_length(special_authors, 1) THEN
                    current_author_id := special_authors[(i - 1) / 10000 + 1];
                    book_title := special_books[(i - 1) / 10000 + 1];
                ELSE
                    current_author_id := 1 + floor(random() * author_count)::INT % author_count;
                    book_title := 'book_' || i;
                END IF;

                INSERT INTO books (title, publication_year, author_id)
                VALUES (book_title, 1900 + (i % 126), current_author_id);
            END LOOP;
    END
$$;
