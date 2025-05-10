package com.tech.afa.archangel.parser.integration;

import com.tech.afa.archangel.parser.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateIntegrationTest extends BaseIntegrationTest {

    @Test
    public void test() throws SQLException {
        String sql = "update authors set name = 'qww'";
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.execute(sql);
        }
    }
}
