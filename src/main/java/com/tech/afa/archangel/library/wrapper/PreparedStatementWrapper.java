package com.tech.afa.archangel.library.wrapper;

import com.tech.afa.archangel.library.interceptor.Interceptor;
import com.tech.afa.archangel.library.utils.Pair;
import com.tech.afa.archangel.library.utils.SqlUtils;
import com.tech.afa.archangel.library.utils.Timer;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PreparedStatementWrapper implements PreparedStatement {

    private final String nativeSql;
    private final Interceptor interceptor;
    private final PreparedStatement originalPreparedStatement;

    private final Map<Integer, Object> parameters = new HashMap<>();

    @Override
    public ResultSet executeQuery() throws SQLException {
        Pair<ResultSet, Long> result = Timer.measure(originalPreparedStatement::executeQuery);
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(nativeSql, parameters), result.second());
        return result.first();
    }

    @Override
    public int executeUpdate() throws SQLException {
        Pair<Integer, Long> result = Timer.measure(originalPreparedStatement::executeUpdate);
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(nativeSql, parameters), result.second());
        return result.first();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters.put(parameterIndex, sqlType);
        originalPreparedStatement.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBoolean(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setByte(parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setShort(parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setInt(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setLong(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setFloat(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setDouble(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setString(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBytes(parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setTimestamp(parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setUnicodeStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void clearParameters() throws SQLException {
        originalPreparedStatement.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setObject(parameterIndex, x);
    }

    @Override
    public boolean execute() throws SQLException {
        return originalPreparedStatement.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        originalPreparedStatement.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setRef(parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBlob(parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setClob(parameterIndex, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return originalPreparedStatement.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setDate(parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setTime(parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameters.put(parameterIndex, sqlType);
        originalPreparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return originalPreparedStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setRowId(parameterIndex, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        parameters.put(parameterIndex, value);
        originalPreparedStatement.setNString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        parameters.put(parameterIndex, value);
        originalPreparedStatement.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        parameters.put(parameterIndex, value);
        originalPreparedStatement.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setClob(parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        parameters.put(parameterIndex, inputStream);
        originalPreparedStatement.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setNClob(parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        parameters.put(parameterIndex, xmlObject);
        originalPreparedStatement.setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setAsciiStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.put(parameterIndex, x);
        originalPreparedStatement.setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        parameters.put(parameterIndex, value);
        originalPreparedStatement.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        parameters.put(parameterIndex, inputStream);
        originalPreparedStatement.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.put(parameterIndex, reader);
        originalPreparedStatement.setNClob(parameterIndex, reader);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        Pair<ResultSet, Long> result = Timer.measure(() -> originalPreparedStatement.executeQuery(sql));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        Pair<Integer, Long> result = Timer.measure(() -> originalPreparedStatement.executeUpdate(sql));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public void close() throws SQLException {
        originalPreparedStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return originalPreparedStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        originalPreparedStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return originalPreparedStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        originalPreparedStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        originalPreparedStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return originalPreparedStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        originalPreparedStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        originalPreparedStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return originalPreparedStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        originalPreparedStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        originalPreparedStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        Pair<Boolean, Long> result = Timer.measure(() -> originalPreparedStatement.execute(sql));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return originalPreparedStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return originalPreparedStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return originalPreparedStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        originalPreparedStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return originalPreparedStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        originalPreparedStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return originalPreparedStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return originalPreparedStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return originalPreparedStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters));
        originalPreparedStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        originalPreparedStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return originalPreparedStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return originalPreparedStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return originalPreparedStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return originalPreparedStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        Pair<Integer, Long> result = Timer.measure(() -> originalPreparedStatement.executeUpdate(sql, autoGeneratedKeys));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        Pair<Integer, Long> result = Timer.measure(() -> originalPreparedStatement.executeUpdate(sql, columnIndexes));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        Pair<Integer, Long> result = Timer.measure(() -> originalPreparedStatement.executeUpdate(sql, columnNames));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        Pair<Boolean, Long> result = Timer.measure(() -> originalPreparedStatement.execute(sql, autoGeneratedKeys));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        Pair<Boolean, Long> result = Timer.measure(() -> originalPreparedStatement.execute(sql, columnIndexes));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        Pair<Boolean, Long> result = Timer.measure(() -> originalPreparedStatement.execute(sql, columnNames));
        interceptor.intercept(nativeSql, SqlUtils.resolveSqlParametersByIndex(sql, parameters), result.second());
        return result.first();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return originalPreparedStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return originalPreparedStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        originalPreparedStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return originalPreparedStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        originalPreparedStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return originalPreparedStatement.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return originalPreparedStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return originalPreparedStatement.isWrapperFor(iface);
    }
}
