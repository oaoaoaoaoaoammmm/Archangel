package com.tech.afa.archangel.library.wrapper;

import com.tech.afa.archangel.library.interceptor.Interceptor;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceWrapper extends DelegatingDataSource {

    private final Interceptor interceptor;

    public DataSourceWrapper(DataSource originalDataSource, Interceptor interceptor) {
        super(originalDataSource);
        this.interceptor = interceptor;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new ConnectionWrapper(interceptor, super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new ConnectionWrapper(interceptor, super.getConnection(username, password));
    }
}
