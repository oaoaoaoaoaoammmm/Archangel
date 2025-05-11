package com.tech.afa.archangel.parser.integration;

import com.tech.afa.archangel.parser.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IntegrationTest extends BaseIntegrationTest {

    @Test
    public void default_test_no_advices() {
        String sql = "select name, country, birth_date from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    @Disabled
    public void advice_about_star() {
        String sql = "select * from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_unused_fields() {
        String sql2 = "select country, birth_date from authors where author_id = ?;";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_unused_fields_for_some_requests() {
        String sql = "select birth_date from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        String sql2 = "select name from authors where author_id = ?;";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advice_about_unused_indexes_on_authors_table_because_it_small() {
        String sql = "create index idx_author_name on authors(name);";
        executeCommand(sql);
        this.forceRefreshSchema();
        String sql2 = "select name, country, birth_date from authors where author_id = ?;";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    @Disabled
    public void advice_about_unused_index() {
        String sql = "create index idx_books_title on books(title);";
        executeCommand(sql);
        this.forceRefreshSchema();
        String sql2 = "select title, publication_year, author_id from books where book_id = ?;";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_add_index_on_title_unused_fields_and_index() {
        String sql = """
        select name, country, birth_date
        from authors
        where author_id = (select b.author_id
                           from books b
                           where title = ?);
        """;
        executeQuery(sql, ps -> ps.setString(1, "Война и мир"), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_large_result() {
        String sql = "select author_id, name, country, birth_date from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        String sql2 = "select author_id, name, country, birth_date from authors;";
        executeQuery(sql2, ps -> {}, this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_transform_function_and_unused_index() {
        String sql = "create index idx_author_name on authors(name);";
        executeCommand(sql);
        this.forceRefreshSchema();
        String sql2 = "select author_id, name, country, birth_date from authors where lower(name) = ?;";
        executeQuery(sql2, ps -> ps.setString(1, "Лев Толстой"), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_add_index_on_joining_field_and_improve_performance() {
        String sql2 = """
        select name, country, birth_date
        from authors a
        join books b on a.author_id = b.author_id
        where a.author_id = ?;""";
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        String sql = "create index idx_books_author_id on books(author_id);";
        executeCommand(sql);
        this.forceRefreshSchema();
        executeQuery(sql2, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_star_full_table_scan_detected_heavy_join() {
        String sql = "select name, country, birth_date from authors where author_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 1), this.getEmptyResultSetMapper());
        String sql2 = "select * from authors, books;";
        executeQuery(sql2, ps -> {}, this.getEmptyResultSetMapper());
    }

    @Test
    public void advices_about_add_index_and_improve_performance_after_adding_it() {
        String sql = "select title, publication_year, author_id from books where book_id = ?;";
        executeQuery(sql, ps -> ps.setInt(1, 777_77), this.getEmptyResultSetMapper());
        String sql2 = "select book_id, publication_year, author_id from books where title = ?;";
        executeQuery(sql2, ps -> ps.setString(1, "Евгений Онегин"), this.getEmptyResultSetMapper());
        String sql3 = "create index idx_books_title on books(title);";
        executeCommand(sql3);
        this.forceRefreshSchema();
        executeQuery(sql2, ps -> ps.setString(1, "Евгений Онегин"), this.getEmptyResultSetMapper());
    }
}
