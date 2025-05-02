package com.tech.afa.archangel.library.utils;

import java.sql.SQLException;

@FunctionalInterface
public interface TimedOperation<T> {

    T execute() throws SQLException;
}
