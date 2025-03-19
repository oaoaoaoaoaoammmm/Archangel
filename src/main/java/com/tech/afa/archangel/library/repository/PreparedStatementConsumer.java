package com.tech.afa.archangel.library.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
interface PreparedStatementConsumer {

    void accept(PreparedStatement ps) throws SQLException;
}
