package com.tech.afa.archangel.parser;

import com.tech.afa.archangel.library.Archangel;
import com.tech.afa.archangel.library.config.ArchangelConfigurationProperties;
import com.tech.afa.archangel.library.config.TriggerMode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseIntegrationTest {

    private static DataSource dataSource;

    private static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("archangel")
            .withUsername("username")
            .withPassword("password")
            .withInitScript("schema-init.sql");

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void executeSql(String sql) {
        try (
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeQuery(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @BeforeAll
    protected static void startContainer() {
        postgres.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(10);
        DataSource ds = new HikariDataSource(config);
        dataSource = new Archangel(ds, "public",new ArchangelConfigurationProperties())
            .getWrapperDataSource();
    }

    @AfterAll
    protected static void stopContainer() {
        postgres.stop();
    }
}
