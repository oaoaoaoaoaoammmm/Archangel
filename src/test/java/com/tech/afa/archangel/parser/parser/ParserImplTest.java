package com.tech.afa.archangel.parser.parser;

import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.model.enums.Condition;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.enums.SQLJoinType;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLGroupBy;
import com.tech.afa.archangel.library.model.request.SQLJoin;
import com.tech.afa.archangel.library.model.request.SQLOrderBy;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.parser.ParserImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserImplTest {

    private final ParserImpl parser = new ParserImpl();

    @Test
    public void test_select_sql1() {
        String sql = """
            SELECT * FROM library.authors;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql1", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql1");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));
    }

    @Test
    public void test_select_sql2() {
        String sql = """
            SELECT * FROM library.books;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql2", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql2");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));
    }

    @Test
    public void test_select_sql3() {
        String sql = """
            SELECT id, name, date FROM library.books ORDER BY date DESC LIMIT 10;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql3", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql3");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("id", "name", "date"));
        assertEquals(sqlRequest.getOrderBy(), List.of(new SQLOrderBy("date", false)));
        assertEquals(sqlRequest.getLimit(), 10);
    }

    @Test
    public void test_select_sql4() {
        String sql = """
            SELECT * FROM library.authors WHERE name LIKE 'А%';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql4", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql4");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "name LIKE 'А%'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "name");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.LIKE);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "'А%'");


    }

    @Test
    public void test_select_sql5() {
        String sql = """
            SELECT b.id, b.name, a.name AS author FROM library.books b JOIN library.authors a ON b.author_id = a.id;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql5", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql5");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("books.id", "books.name", "authors.name"));
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "authors");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "books.author_id = authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "authors.id");

    }

    @Test
    public void test_select_sql6() {
        String sql = """
            SELECT b.name, b.date FROM library.books b JOIN library.authors a ON b.author_id = a.id WHERE a.name = 'Лев Толстой';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql6", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql6");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("books.name", "books.date"));
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "authors");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "books.author_id = authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "authors.id");

        assertEquals(sqlRequest.getWhereCondition().getExpression(), "authors.name = 'Лев Толстой'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "authors.name");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "'Лев Толстой'");
    }

    @Test
    public void test_select_sql7() {
        String sql = """
            SELECT a.name, COUNT(b.id) AS book_count FROM library.authors a LEFT JOIN library.books b ON a.id = b.author_id GROUP BY a.name ORDER BY book_count DESC;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql7", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql7");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("authors.name", "COUNT(books.id)"));
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "books");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "authors.id = books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "books.author_id");
        assertEquals(sqlRequest.getGroupBy(), List.of(new SQLGroupBy("authors.name")));
        assertEquals(sqlRequest.getOrderBy(), List.of(new SQLOrderBy("book_count", false)));
    }

    @Test
    public void test_select_sql8() {
        String sql = """
            SELECT * FROM library.books WHERE author_id IN (SELECT author_id FROM library.books GROUP BY author_id HAVING COUNT(*) > 5);
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql8", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql8");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));
        assertTrue(sqlRequest.getWhereCondition().getExpression().startsWith("author_id IN"));

        SQLRequest subQuery = sqlRequest.getWhereCondition().getSubSelect();
        assertNotNull(subQuery);
        assertEquals(subQuery.getId(), "sql8_in_sub");
        assertEquals(subQuery.getNativeSql(), "(SELECT author_id FROM library.books GROUP BY author_id HAVING COUNT(*) > 5)");
        assertEquals(subQuery.getCommandType(), SQLCommandType.SELECT);
        assertEquals(subQuery.getTables(), List.of("books"));
        assertEquals(subQuery.getColumns(), List.of("author_id"));
        assertNotNull(subQuery.getHavingCondition());
    }

    @Test
    public void test_select_sql9() {
        String sql = """
            SELECT * FROM library.authors a WHERE NOT EXISTS (SELECT 1 FROM library.books b WHERE b.author_id = a.id);
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql9", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql9");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));
        assertTrue(sqlRequest.getWhereCondition().getExpression().startsWith("NOT EXISTS"));
        assertNotNull(sqlRequest.getWhereCondition().getSubSelect());

        SQLRequest subQuery = sqlRequest.getWhereCondition().getSubSelect();
        assertEquals(subQuery.getId(), "sql9_sub_sub");
        assertEquals(subQuery.getNativeSql(), "(SELECT 1 FROM library.books b WHERE b.author_id = a.id)");
        assertEquals(subQuery.getCommandType(), SQLCommandType.SELECT);
        assertEquals(subQuery.getTables(), List.of("books"));
        assertEquals(subQuery.getColumns(), List.of("1"));
        assertNotNull(subQuery.getWhereCondition());
        assertEquals(subQuery.getWhereCondition().getFieldName(), "books.author_id");
        assertEquals(subQuery.getWhereCondition().getCondition(), Condition.EQUALS);
        assertEquals(subQuery.getWhereCondition().getValue(), "authors.id");
    }

    @Test
    public void test_select_sql10() {
        String sql = """
            SELECT a.name, AVG(b.le) AS avg_le FROM library.authors a JOIN library.books b ON a.id = b.author_id GROUP BY a.name HAVING AVG(b.le) > 10;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql10", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);
        
        assertEquals(sqlRequest.getId(), "sql10");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("authors.name", "AVG(books.le)"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        SQLJoin join = sqlRequest.getJoins().getFirst();
        assertEquals(join.getJoinedTable(), "books");
        assertEquals(join.getType(), SQLJoinType.UNKNOWN);
        assertEquals(join.getCondition().getExpression(), "authors.id = books.author_id");
        assertEquals(join.getCondition().getFieldName(), "authors.id");
        assertEquals(join.getCondition().getCondition(), Condition.EQUALS);
        assertEquals(join.getCondition().getValue(), "books.author_id");

        assertNotNull(sqlRequest.getGroupBy());
        assertEquals(sqlRequest.getGroupBy().size(), 1);
        assertEquals(sqlRequest.getGroupBy().getFirst().getColumn(), "authors.name");

        assertNotNull(sqlRequest.getHavingCondition());
        assertEquals(sqlRequest.getHavingCondition().getExpression(), "AVG(books.le) > 10");
    }

    @Test
    public void test_select_sql11() {
        String sql = """
            SELECT EXTRACT(YEAR FROM date) AS year, COUNT(*) AS books_published
            FROM library.books
            GROUP BY EXTRACT(YEAR FROM date)
            ORDER BY year;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql11", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql11");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("EXTRACT(YEAR FROM date)", "COUNT(*)"));

        assertNotNull(sqlRequest.getGroupBy());
        assertEquals(sqlRequest.getGroupBy().size(), 1);
        assertEquals(sqlRequest.getGroupBy().getFirst().getColumn(), "EXTRACT(YEAR FROM date)");

        assertNotNull(sqlRequest.getOrderBy());
        assertEquals(sqlRequest.getOrderBy().size(), 1);
        assertEquals(sqlRequest.getOrderBy().getFirst().getColumn(), "year");
        assertTrue(sqlRequest.getOrderBy().getFirst().isAscending());
    }

    @Test
    public void test_select_sql12() {
        String sql = """
            SELECT name, date
            FROM library.books
            WHERE le > 15 AND le < 30;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql12", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql12");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("name", "date"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "le > 15 AND le < 30");
        assertNotNull(sqlRequest.getWhereCondition().getAndConditions());
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().size(), 2);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getExpression(), "le > 15");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getFieldName(), "le");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getCondition(), Condition.GREATER);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getValue(), "15");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getExpression(), "le < 30");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getFieldName(), "le");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getCondition(), Condition.LESS);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getValue(), "30");
    }

    @Test
    public void test_select_sql13() {
        String sql = """
            SELECT b.name, a.name AS author
            FROM library.books b
            JOIN library.authors a ON b.author_id = a.id
            WHERE a.name = 'Фёдор Достоевский'
            ORDER BY b.name;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql13", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql13");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("books.name", "authors.name"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "authors");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "books.author_id = authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "authors.id");
        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "authors.name = 'Фёдор Достоевский'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "authors.name");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "'Фёдор Достоевский'");
        assertNotNull(sqlRequest.getOrderBy());
        assertEquals(sqlRequest.getOrderBy().size(), 1);
        assertEquals(sqlRequest.getOrderBy().getFirst().getColumn(), "books.name");
        assertTrue(sqlRequest.getOrderBy().getFirst().isAscending());
    }

    @Test
    public void test_select_sql15() {
        String sql = """
            SELECT
              a.name AS author,
              b.name AS book,
              b.le,
              RANK() OVER (PARTITION BY a.id ORDER BY b.le DESC) AS le_rank
            FROM library.books b
            JOIN library.authors a ON b.author_id = a.id;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql15", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql15");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("authors.name", "books.name", "books.le", "RANK() OVER (PARTITION BY a.id ORDER BY b.le DESC)"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "authors");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "books.author_id = authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "authors.id");
        assertTrue(sqlRequest.getColumns().contains("RANK() OVER (PARTITION BY a.id ORDER BY b.le DESC)"));
    }

    @Test
    public void test_select_sql16() {
        String sql = """
            SELECT
              b.name,
              a.name AS author,
              b.le,
              AVG(b.le) OVER (PARTITION BY b.author_id) AS author_avg_le,
              b.le - AVG(b.le) OVER (PARTITION BY b.author_id) AS diff_from_avg
            FROM library.books b
            JOIN library.authors a ON b.author_id = a.id;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql16", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql16");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("books.name", "authors.name", "books.le", "AVG(b.le) OVER (PARTITION BY b.author_id )", "b.le - AVG(b.le) OVER (PARTITION BY b.author_id )"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "authors");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "books.author_id = authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "authors.id");

        assertTrue(sqlRequest.getColumns().contains("AVG(b.le) OVER (PARTITION BY b.author_id )"));
        assertTrue(sqlRequest.getColumns().contains("b.le - AVG(b.le) OVER (PARTITION BY b.author_id )"));
    }

    @Test
    public void test_select_sql17() {
        String sql = """
            SELECT *
            FROM library.books
            WHERE date >= NOW() - INTERVAL '1 month';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql17", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql17");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "date >= NOW() - INTERVAL '1 month'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "date");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.GREATER_OR_EQUAL);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "NOW() - INTERVAL '1 month'");
    }

    @Test
    public void test_select_sql18() {
        String sql = """
            SELECT DISTINCT ON (a.id)
            a.name AS author,
            b.name AS oldest_book,
            b.date
            FROM library.authors a
            JOIN library.books b ON a.id = b.author_id
            ORDER BY a.id, b.date ASC;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql18", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql18");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("authors.name", "books.name", "books.date"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "books");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "authors.id = books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "books.author_id");

        assertNotNull(sqlRequest.getOrderBy());
        assertEquals(sqlRequest.getOrderBy().size(), 2);
        assertEquals(sqlRequest.getOrderBy().getFirst().getColumn(), "authors.id");
        assertTrue(sqlRequest.getOrderBy().getFirst().isAscending());
        assertEquals(sqlRequest.getOrderBy().get(1).getColumn(), "books.date");
        assertTrue(sqlRequest.getOrderBy().get(1).isAscending());
    }

    @Test
    public void test_select_sql19() {
        String sql = """
            SELECT * FROM library.books
            WHERE le > 20 OR date >= NOW() - INTERVAL '6 months';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql19", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql19");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("*"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "le > 20 OR date >= NOW() - INTERVAL '6 months'");

        assertNotNull(sqlRequest.getWhereCondition().getOrConditions());
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().size(), 2);
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(0).getExpression(), "le > 20");
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(0).getFieldName(), "le");
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(0).getCondition(), Condition.GREATER);
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(0).getValue(), "20");
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(1).getExpression(), "date >= NOW() - INTERVAL '6 months'");
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(1).getFieldName(), "date");
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(1).getCondition(), Condition.GREATER_OR_EQUAL);
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(1).getValue(), "NOW() - INTERVAL '6 months'");
    }

    @Test
    public void test_select_sql20() {
        String sql = """
            SELECT DISTINCT a.*
            FROM library.authors a
            JOIN library.books b ON a.id = b.author_id
            WHERE b.le > 15 AND b.existss = true;
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql20", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql20");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.SELECT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("a.*"));

        assertNotNull(sqlRequest.getJoins());
        assertEquals(sqlRequest.getJoins().size(), 1);
        assertEquals(sqlRequest.getJoins().getFirst().getJoinedTable(), "books");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getExpression(), "authors.id = books.author_id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getFieldName(), "authors.id");
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getJoins().getFirst().getCondition().getValue(), "books.author_id");

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "books.le > 15 AND books.existss = true");

        assertNotNull(sqlRequest.getWhereCondition().getAndConditions());
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().size(), 2);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getExpression(), "books.le > 15");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getFieldName(), "books.le");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getCondition(), Condition.GREATER);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().getFirst().getValue(), "15");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getExpression(), "books.existss = true");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getFieldName(), "books.existss");
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().get(1).getValue(), "true");
    }

    @Test
    public void test_insert_sql1() {
        String sql = """
                INSERT INTO library.authors (name)
                VALUES ('Александр Пушкин');
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql1", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql1");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.INSERT);
        assertEquals(sqlRequest.getTables(), List.of("authors"));
        assertEquals(sqlRequest.getColumns(), List.of("name"));

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 1);
        assertEquals(sqlRequest.getValues().getFirst().size(), 1);
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getRawValue(), "'Александр Пушкин'");
    }

    @Test
    public void test_insert_sql2() {
        String sql = """
                INSERT INTO library.books (name, author_id, date, existss, le)
                VALUES
                ('Евгений Онегин', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2023-01-15', true, 12),
                ('Капитанская дочка', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2023-02-20', true, 8);
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql2", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql2");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.INSERT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("name", "author_id", "date", "existss", "le"));

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 2);
        assertEquals(sqlRequest.getValues().getFirst().size(), 5);
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getRawValue(), "'Евгений Онегин'");
        assertEquals(sqlRequest.getValues().getFirst().get(1).getRawValue(), "'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");
        assertEquals(sqlRequest.getValues().getFirst().get(2).getRawValue(), "'2023-01-15'");
        assertEquals(sqlRequest.getValues().getFirst().get(3).getRawValue(), "true");
        assertEquals(sqlRequest.getValues().getFirst().get(4).getRawValue(), "12");

        assertEquals(sqlRequest.getValues().get(1).size(), 5);
        assertEquals(sqlRequest.getValues().get(1).getFirst().getRawValue(), "'Капитанская дочка'");
        assertEquals(sqlRequest.getValues().get(1).get(1).getRawValue(), "'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");
        assertEquals(sqlRequest.getValues().get(1).get(2).getRawValue(), "'2023-02-20'");
        assertEquals(sqlRequest.getValues().get(1).get(3).getRawValue(), "true");
        assertEquals(sqlRequest.getValues().get(1).get(4).getRawValue(), "8");
    }

    @Test
    public void test_insert_sql3() {
        String sql = """
                INSERT INTO library.books (name, author_id, date, existss, le)
                VALUES
                ('Евгений Онегин', (SELECT id FROM library.authors WHERE name = 'Александр Пушкин'), '2023-01-15', true, 12),
                ('Капитанская дочка', (SELECT id FROM library.authors WHERE name = 'Александр Пушкин'), '2023-02-20', true, 8);
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql3", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql3");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.INSERT);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("name", "author_id", "date", "existss", "le"));

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 2);
        assertEquals(sqlRequest.getValues().getFirst().size(), 5);
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getRawValue(), "'Евгений Онегин'");

        assertNotNull(sqlRequest.getValues().getFirst().get(1).getSelectRequest());
        assertEquals(sqlRequest.getValues().getFirst().get(1).getSelectRequest().getId(), "sql3_sub");
        assertEquals(sqlRequest.getValues().getFirst().get(1).getSelectRequest().getNativeSql(),
            "(SELECT id FROM library.authors WHERE name = 'Александр Пушкин')");
        assertEquals(sqlRequest.getValues()
            .getFirst()
            .get(1)
            .getSelectRequest()
            .getCommandType(), SQLCommandType.SELECT);

        assertEquals(sqlRequest.getValues().getFirst().get(2).getRawValue(), "'2023-01-15'");
        assertEquals(sqlRequest.getValues().getFirst().get(3).getRawValue(), "true");
        assertEquals(sqlRequest.getValues().getFirst().get(4).getRawValue(), "12");

        assertEquals(sqlRequest.getValues().get(1).size(), 5);
        assertEquals(sqlRequest.getValues().get(1).getFirst().getRawValue(), "'Капитанская дочка'");

        assertNotNull(sqlRequest.getValues().get(1).get(1).getSelectRequest());
        assertEquals(sqlRequest.getValues().get(1).get(1).getSelectRequest().getId(), "sql3_sub");
        assertEquals(sqlRequest.getValues().get(1).get(1).getSelectRequest().getNativeSql(),
            "(SELECT id FROM library.authors WHERE name = 'Александр Пушкин')");
        assertEquals(sqlRequest.getValues().get(1).get(1).getSelectRequest().getCommandType(), SQLCommandType.SELECT);

        assertEquals(sqlRequest.getValues().get(1).get(2).getRawValue(), "'2023-02-20'");
        assertEquals(sqlRequest.getValues().get(1).get(3).getRawValue(), "true");
        assertEquals(sqlRequest.getValues().get(1).get(4).getRawValue(), "8");
    }

    @Test
    public void test_update_sql1() {
        String sql = """
            UPDATE library.books
            SET existss = false
            WHERE date < '1900-01-01';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql1", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql1");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.UPDATE);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("existss"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "date < '1900-01-01'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "date");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.LESS);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "'1900-01-01'");

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 1);
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getRawValue(), "false");
    }

    @Test
    public void test_update_sql2() {
        String sql = """
            UPDATE library.books
            SET author_id = (
                SELECT id FROM library.authors WHERE name = 'Александр Пушкин'
            )
            WHERE name = 'Евгений Онегин';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql2", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql2");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.UPDATE);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("author_id"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "name = 'Евгений Онегин'");
        assertEquals(sqlRequest.getWhereCondition().getFieldName(), "name");
        assertEquals(sqlRequest.getWhereCondition().getCondition(), Condition.EQUALS);
        assertEquals(sqlRequest.getWhereCondition().getValue(), "'Евгений Онегин'");

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 1);
        assertNotNull(sqlRequest.getValues().getFirst().getFirst().getSelectRequest());
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getSelectRequest().getTables(), List.of("authors"));
    }

    @Test
    public void test_update_sql3() {
        String sql = """
            UPDATE library.books
            SET existss = true, date = NOW()
            WHERE author_id IN (
                SELECT id FROM library.authors WHERE existss = true
            )
            AND EXISTS (
                SELECT 1 FROM library.publishers WHERE library.publishers.id = library.books.le
            );
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql3", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql3");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.UPDATE);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("existss", "date"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getAndConditions().size(), 2);

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 1);
        assertEquals(sqlRequest.getValues().getFirst().size(), 2);
        assertEquals(sqlRequest.getValues().getFirst().get(0).getRawValue(), "true");
        assertEquals(sqlRequest.getValues().getFirst().get(1).getRawValue(), "NOW()");
    }

    @Test
    public void test_update_sql4() {
        String sql = """
            UPDATE books
            SET existss = (SELECT boo FROM orders o WHERE o.book_id = books.id)
            WHERE date < '1900-01-01';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql4", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql4");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.UPDATE);
        assertEquals(sqlRequest.getTables(), List.of("books"));
        assertEquals(sqlRequest.getColumns(), List.of("existss"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "date < '1900-01-01'");

        assertNotNull(sqlRequest.getValues());
        assertEquals(sqlRequest.getValues().size(), 1);
        assertNotNull(sqlRequest.getValues().getFirst().getFirst().getSelectRequest());
        assertEquals(sqlRequest.getValues().getFirst().getFirst().getSelectRequest().getTables(), List.of("orders"));
    }

    @Test
    public void test_delete_sql1() {
        String sql = """
            DELETE FROM library.books
            WHERE date < '1800-01-01';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql1", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql1");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.DELETE);
        assertEquals(sqlRequest.getTables(), List.of("books"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition().getExpression(), "date < '1800-01-01'");
    }

    @Test
    public void test_delete_sql2() {
        String sql = """
            DELETE FROM library.books
            WHERE author_id IN (
                SELECT id FROM library.authors
                WHERE deceased = true
            );
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql2", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql2");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.DELETE);
        assertEquals(sqlRequest.getTables(), List.of("books"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition()
            .getExpression(), "author_id IN (SELECT id FROM library.authors WHERE deceased = true)");

        SQLRequest subSelect = sqlRequest.getWhereCondition().getSubSelect();
        assertNotNull(subSelect);
        assertEquals(subSelect.getCommandType(), SQLCommandType.SELECT);
        assertEquals(subSelect.getTables(), List.of("authors"));
        assertEquals(subSelect.getColumns(), List.of("id"));
        assertEquals(subSelect.getWhereCondition().getExpression(), "deceased = true");
        assertEquals(subSelect.getWhereCondition().getFieldName(), "deceased");
        assertEquals(subSelect.getWhereCondition().getCondition(), Condition.EQUALS);
        assertEquals(subSelect.getWhereCondition().getValue(), "true");
    }

    @Test
    public void test_delete_sql3() {
        String sql = """
            DELETE FROM library.orders
            WHERE EXISTS (
                SELECT 1 FROM library.books
                WHERE library.books.id = library.orders.book_id
                AND library.books.existss = false
            );
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql3", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql3");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.DELETE);
        assertEquals(sqlRequest.getTables(), List.of("orders"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition()
            .getExpression(), "EXISTS (SELECT 1 FROM library.books WHERE library.books.id = library.orders.book_id AND library.books.existss = false)");

        SQLRequest subSelect = sqlRequest.getWhereCondition().getSubSelect();
        assertNotNull(subSelect);
        assertEquals(subSelect.getCommandType(), SQLCommandType.SELECT);
        assertEquals(subSelect.getTables(), List.of("books"));
        assertEquals(subSelect.getColumns(), List.of("1"));

        assertEquals(subSelect.getWhereCondition().getExpression(), "library.books.id = library.orders.book_id AND library.books.existss = false");
        assertNotNull(subSelect.getWhereCondition().getAndConditions());
        assertEquals(subSelect.getWhereCondition().getAndConditions().size(), 2);
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(0).getExpression(), "library.books.id = library.orders.book_id");
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(0).getFieldName(), "id");
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(0).getCondition(), Condition.EQUALS);
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(0).getValue(), "book_id");
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(1).getExpression(), "library.books.existss = false");
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(1).getFieldName(), "existss");
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(1).getCondition(), Condition.EQUALS);
        assertEquals(subSelect.getWhereCondition().getAndConditions().get(1).getValue(), "false");
    }

    @Test
    public void test_delete_sql4() {
        String sql = """
            DELETE FROM library.publishers
            WHERE name IS NULL OR name = '' AND created_at < '1900-01-01';
            """;
        SQLRequestView sqlRequestView = new SQLRequestView("sql4", sql);

        SQLRequest sqlRequest = parser.parse(sqlRequestView);

        assertEquals(sqlRequest.getId(), "sql4");
        assertEquals(sqlRequest.getNativeSql(), sql);
        assertEquals(sqlRequest.getCommandType(), SQLCommandType.DELETE);
        assertEquals(sqlRequest.getTables(), List.of("publishers"));

        assertNotNull(sqlRequest.getWhereCondition());
        assertEquals(sqlRequest.getWhereCondition()
            .getExpression(), "name IS NULL OR name = '' AND created_at < '1900-01-01'");

        assertNotNull(sqlRequest.getWhereCondition().getOrConditions());
        assertEquals(sqlRequest.getWhereCondition().getOrConditions().size(), 2);

        assertEquals(sqlRequest.getWhereCondition().getOrConditions().get(0).getExpression(), "name IS NULL");
        SQLCondition secondOr = sqlRequest.getWhereCondition().getOrConditions().get(1);
        assertEquals(secondOr.getExpression(), "name = '' AND created_at < '1900-01-01'");

        assertNotNull(secondOr.getAndConditions());
        assertEquals(secondOr.getAndConditions().size(), 2);
        assertEquals(secondOr.getAndConditions().get(0).getExpression(), "name = ''");
        assertEquals(secondOr.getAndConditions().get(0).getFieldName(), "name");
        assertEquals(secondOr.getAndConditions().get(0).getCondition(), Condition.EQUALS);
        assertEquals(secondOr.getAndConditions().get(0).getValue(), "''");
        assertEquals(secondOr.getAndConditions().get(1).getExpression(), "created_at < '1900-01-01'");
    }
}
