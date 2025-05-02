package com.tech.afa.archangel.library.repository;

import com.tech.afa.archangel.library.utils.SqlLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
abstract class Repository {

    private final DataSource dataSource;

    protected String executeQuery(String sql) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
        ) {
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append(rs.getString(1)).append("\n");
            }
            /*
            while (rs.next()) {
                try (InputStream is = rs.getAsciiStream(1);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
             */
            return result.toString();
        } catch (SQLException ex) {
            log.error("Error executing SQL", ex);
            throw new RuntimeException("Failed to execute sql", ex);
        }
    }

    protected <T> List<T> executeQuery(
        String sqlFile,
        PreparedStatementConsumer preparer,
        ResultSetMapper<T> mapper
    ) {
        String sql = SqlLoader.loadSql(sqlFile);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preparer.accept(ps);
            List<T> results = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
            return results;
        } catch (SQLException ex) {
            log.error("Error executing SQL from {}", sqlFile, ex);
            throw new RuntimeException("Failed to execute query from " + sqlFile, ex);
        }
    }
}
