package com.tech.afa.archangel.parser.integration;

import com.tech.afa.archangel.parser.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

public class SelectIntegrationTest extends BaseIntegrationTest {

    @Test
    public void default_test_no_advices() {
        String sql = "select name, country, birth_date from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_star() {
        String sql = "select * from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_unused_fields() {
        String sql2 = "select country, birth_date from authors where author_id = ?;";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_unused_fields_for_some_requests() {
        String sql = "select birth_date from authors where author_id = ?;";
        String sql2 = "select name from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_unused_indexes_on_authors_table_because_it_small() {
        String sql = "create index idx_author_name on authors(name);";
        String sql2 = "select name, country, birth_date from authors where author_id = ?;";
        executeCommand(sql);
        this.forceRefreshSchema();
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_unused_index() {
        String sql = "create index idx_books_title on books(title);";
        String sql2 = "select title, publication_year, author_id from books where book_id = ?;";
        executeCommand(sql);
        this.forceRefreshSchema();
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        //executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    /*
    @Test
    public void advices_about_unused_index() {
        String sql = "create index idx_author_name on authors(name);";
        String sql2 = "select name, country, birth_date from authors where author_id = ?;";
        executeCommand(sql);
        this.forceRefreshSchema();
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_add_index() {
        String sql = "select country, birth_date from authors where author_id = ?;";
        String sql2 = "select author_id, country, birth_date from authors where name = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        executeQuery(sql2, ps -> ps.setString(1, "Лев Толстой"), this.getEmptyResultSetMapper());
        executeQuery(sql2, ps -> ps.setString(1, "Лев Толстой"), this.getEmptyResultSetMapper());
    }

     */
    /*
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

     */
}
