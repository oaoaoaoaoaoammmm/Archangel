package com.tech.afa.archangel.parser;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestClass extends BaseIntegrationTest {

    @Test
    public void test() throws SQLException {
        String sql = "select * from authors where author_id = 3";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0() throws SQLException {
        String sql = "select * from authors where name = 'author name'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0_0() throws SQLException {
        String sql = "select * from authors where author_id = 1 and name = 'author name' or country = 'count'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0_0_0() throws SQLException {
        String sql = """
            SELECT a.author_id, a.name, b.title FROM authors a, books b WHERE a.author_id = b.author_id AND a.name = 'author name' and b.title = 'title';
            """;
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0_0_0_0() throws SQLException {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and a.country = 'qwe' and b.title = 'title'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0_0_0_0_0() throws SQLException {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and b.title = 'title'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_0_0_0_0_0_0() throws SQLException {
        String sql = "SELECT * FROM authors a JOIN books b ON a.author_id = b.author_id limit 5000";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1() throws SQLException {
        String sql = "SELECT * FROM authors ORDER BY birth_date, country LIMIT 10;";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1_0() throws SQLException {
        String sql = "SELECT a.author_id, a.name AS author_name, b.title AS book_title, b.publication_year FROM authors a JOIN books b ON a.author_id = b.author_id where a.name = 'author name' and a.country = 'qwe' and b.title = 'title' order by a.birth_date, a.country";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1_0_0() throws SQLException {
        String sql = "SELECT publication_year, COUNT(*) FROM books GROUP BY publication_year HAVING publication_year > 2000;";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1_0_0_0() throws SQLException {
        String sql = "SELECT * FROM authors WHERE name = '123' OR birth_date >= NOW() - INTERVAL '6 months';";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1_0_0_0_0() throws SQLException {
        String sql = "SELECT EXTRACT(YEAR FROM birth_date) AS year, COUNT(*) AS books_published FROM authors GROUP BY EXTRACT(YEAR FROM birth_date) ORDER BY year";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }

    @Test
    public void test_1_0_0_0_0_0() throws SQLException {
        String sql = "select * from authors where lower(name) = 'author name'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        }
    }


    @Test
    public void test2() throws SQLException {
        String sql = "update authors set name = 'qww'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.execute(sql);
        }
    }

    @Test
    public void test3() throws SQLException {
        String sql = "select * from authors";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
        }
    }

    @Test
    public void test4() throws SQLException {
        String sql = "select * from authors";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
        }
    }
}
