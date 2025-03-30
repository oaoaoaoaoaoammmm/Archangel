package com.tech.afa.archangel.library.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface ResultSetMapper<T> {

    T map(ResultSet rs) throws SQLException;
}
