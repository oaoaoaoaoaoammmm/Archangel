CREATE TABLE authors
(
    author_id  SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    birth_date DATE,
    country    VARCHAR(50)
);

CREATE INDEX idx_lower_name ON authors (LOWER(name));

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

DO
$$
    DECLARE
        i              INT;
        special_titles TEXT[] := ARRAY [
            'Война и мир - юбилейное издание',
            'Преступление и наказание - коллекционное издание',
            'Старик и море - золотая классика',
            'Анна Каренина - подарочное издание',
            'Братья Карамазовы - полное собрание',
            'По ком звонит колокол - специальный выпуск',
            'Воскресение - памятное издание',
            'Идиот - эксклюзивная версия',
            'Прощай, оружие! - ограниченный тираж',
            'Бесы - авторская редакция'
            ];
        regular_titles TEXT[] := ARRAY [
            'Роман без названия',
            'Неизвестное произведение',
            'Сборник рассказов',
            'Поэмы и стихи',
            'Дневники и письма',
            'Избранные произведения',
            'Полное собрание сочинений',
            'Литературное наследие',
            'Записки писателя',
            'Хрестоматия'
            ];
        book_title     TEXT;
        pub_year       INT;
        author         INT;
    BEGIN
        FOR i IN 1..100000
            LOOP
                IF i % 100000 = 0 THEN
                    book_title := special_titles[(i / 100000)::INT];
                ELSE
                    book_title := regular_titles[1 + floor(random() * array_length(regular_titles, 1))] || ' ' ||
                                  (1000 + floor(random() * 9000))::TEXT;
                END IF;
                pub_year := 1800 + floor(random() * 224)::INT;
                author := 1 + floor(random() * 3)::INT;
                INSERT INTO books (title, publication_year, author_id)
                VALUES (book_title, pub_year, author);
                IF i % 10000 = 0 THEN
                    COMMIT;
                END IF;
            END LOOP;
    END
$$;
