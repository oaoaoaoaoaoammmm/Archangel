package com.tech.afa.archangel.parser.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementConsumer {

    void accept(PreparedStatement ps) throws SQLException;
}
