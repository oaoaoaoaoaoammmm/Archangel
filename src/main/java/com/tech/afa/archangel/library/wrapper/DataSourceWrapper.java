package com.tech.afa.archangel.library.wrapper;

import com.tech.afa.archangel.library.Interceptor;
import com.tech.afa.archangel.library.InterceptorSQL;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceWrapper extends DelegatingDataSource {

    private final Interceptor interceptor;

    public DataSourceWrapper(DataSource originalDataSource) {
        super(originalDataSource);
        this.interceptor = new InterceptorSQL();
    }

    public DataSourceWrapper(DataSource originalDataSource, Interceptor interceptor) {
        super(originalDataSource);
        this.interceptor = interceptor;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new ConnectionWrapper(super.getConnection(), interceptor);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new ConnectionWrapper(super.getConnection(username, password), interceptor);
    }
}
