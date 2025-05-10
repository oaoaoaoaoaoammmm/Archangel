package com.tech.afa.archangel.parser;

import com.tech.afa.archangel.library.Archangel;
import com.tech.afa.archangel.library.config.ArchangelConfigurationProperties;
import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.parser.utils.PreparedStatementConsumer;
import com.tech.afa.archangel.parser.utils.ResultSetMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class BaseIntegrationTest {

    private Archangel archangel;

    private DataSource dataSource;

    private final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("archangel")
            .withUsername("username")
            .withPassword("password")
            .withInitScript("schema-init.sql");

    @BeforeEach
    protected void startContainer() {
        postgres.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(10);
        DataSource ds = new HikariDataSource(config);
        ArchangelConfigurationProperties props = new ArchangelConfigurationProperties();
        props.setSchema("public");
        this.archangel = new Archangel(ds, props);
        this.dataSource = archangel.getWrapperDataSource();
    }

    @AfterEach
    protected void stopContainer() {
        postgres.stop();
    }

    protected void executeCommand(String sql) {
        try (Connection conn = dataSource.getConnection();
             Statement ps = conn.createStatement()) {
            ps.execute(sql);
        } catch (SQLException ex) {
            log.error("Error executing SQL {}", sql, ex);
            throw new RuntimeException("Failed to execute SQL " + sql, ex);
        }
    }

    protected <T> List<T> executeQuery(
        String sql,
        PreparedStatementConsumer preparer,
        ResultSetMapper<T> mapper
    ) {
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
            log.error("Error executing SQL {}", sql, ex);
            throw new RuntimeException("Failed to execute SQL " + sql, ex);
        }
    }

    protected ResultSetMapper<?> getEmptyResultSetMapper() {
        return rs -> null;
    }

    protected void forceRefreshSchema() {
        this.archangel.forceRefreshSchema();
    }
}
