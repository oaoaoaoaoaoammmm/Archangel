package com.tech.afa.archangel.parser.integration;

import com.tech.afa.archangel.parser.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

public class SelectIntegrationTest extends BaseIntegrationTest {

    @Test
    public void test() {
        String sql = "select * from authors where author_id = 3";
        executeSql(sql);
    }

    @Test
    public void test_0() {
        String sql = "select * from authors where name = 'author name'";
        executeSql(sql);
    }

    @Test
    public void test_0_1_0() {
        String sql = "select * from books where title = 'Война и мир - юбилейное издание'";
        executeSql(sql);
    }

    @Test
    public void test_0_1_0_1() {
        String sql = "select * from books where title = 'Война и мир - юбилейное издание' or title = (select title from books where title = 'Бесы - авторская редакция' and publication_year < 3000)";
        executeSql(sql);
    }

    @Test
    public void test_0_0() {
        String sql = "select * from authors where author_id = 1 and name = 'author name' or country = 'count'";
        executeSql(sql);
    }

    @Test
    public void test_0_0_0() {
        String sql = """
            SELECT a.author_id, a.name, b.title FROM authors a, books b WHERE a.author_id = b.author_id AND a.name = 'author name' and b.title = 'title';
            """;
        executeSql(sql);
    }

    @Test
    public void test_0_0_0_0() {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and a.country = 'qwe' and b.title = 'title'";
        executeSql(sql);
    }

    @Test
    public void test_0_0_0_0_0() {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and b.title = 'title'";
        executeSql(sql);
    }

    @Test
    public void test_0_0_0_0_0_0() {
        String sql = "SELECT * FROM authors a JOIN books b ON a.author_id = b.author_id limit 5000";
        executeSql(sql);
    }

    @Test
    public void test_1() {
        String sql = "SELECT * FROM authors ORDER BY birth_date, country LIMIT 10;";
        executeSql(sql);
    }

    @Test
    public void test_1_0() {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and a.country = 'qwe' and b.title = 'title' order by a.birth_date, a.country";
        executeSql(sql);
    }

    @Test
    public void test_1_0_0() {
        String sql = "SELECT publication_year, COUNT(*) FROM books GROUP BY publication_year HAVING publication_year > 2000;";
        executeSql(sql);
    }

    @Test
    public void test_1_0_0_0() {
        String sql = "SELECT * FROM authors WHERE name = '123' OR birth_date >= NOW() - INTERVAL '6 months';";
        executeSql(sql);
    }

    @Test
    public void test_1_0_0_0_0() {
        String sql = "SELECT EXTRACT(YEAR FROM birth_date) AS year, COUNT(*) AS books_published FROM authors GROUP BY EXTRACT(YEAR FROM birth_date) ORDER BY year";
        executeSql(sql);
    }

    @Test
    public void test_1_0_0_0_0_0() {
        String sql = "select * from authors where lower(name) = 'author name'";
        executeSql(sql);
    }
}
