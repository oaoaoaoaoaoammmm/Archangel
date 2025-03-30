package com.tech.afa.archangel.library.repository;

import com.tech.afa.archangel.library.model.ConstraintType;
import com.tech.afa.archangel.library.model.IndexType;
import com.tech.afa.archangel.library.model.Table.Column;
import com.tech.afa.archangel.library.model.Table.Index;
import com.tech.afa.archangel.library.utils.PredicateParser;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemaRepository extends Repository {

    private static final String FIND_TABLES_SQL = "findTables.sql";
    private static final String FIND_COLUMNS_SQL = "findColumns.sql";
    private static final String FIND_INDEXES_SQL = "findIndexes.sql";

    public SchemaRepository(DataSource dataSource) {
        super(dataSource);
    }

    public List<String> loadTables(String schema) {
        return executeQuery(
            FIND_TABLES_SQL,
            ps -> ps.setString(1, schema),
            rs -> rs.getString("table_name")
        );
    }

    public Map<String, List<Column>> loadColumns(String schema) {
        return executeQuery(
            FIND_COLUMNS_SQL,
            ps -> ps.setString(1, schema),
            this::mapToColumn
        ).stream().collect(Collectors.groupingBy(
            TableColumn::tableName,
            Collectors.mapping(TableColumn::column, Collectors.toList())
        ));
    }

    public Map<String, List<Index>> loadIndexes(String schema, String tableName) {
        List<Index> indexes = executeQuery(
            FIND_INDEXES_SQL,
            ps -> {
                ps.setString(1, schema);
                ps.setString(2, tableName);
            },
            this::mapToIndex
        );
        return Map.of(tableName, indexes);
    }

    private TableColumn mapToColumn(ResultSet rs) throws SQLException {
        String tableName = rs.getString("table_name");
        String columnName = rs.getString("column_name");
        String dataType = rs.getString("data_type");
        Set<ConstraintType> constraints = parseConstraintTypes(
            rs.getString("constraint_types")
        );
        return new TableColumn(tableName, new Column(columnName, dataType, constraints));
    }

    private Index mapToIndex(ResultSet rs) throws SQLException {
        return new Index(
            rs.getString("index_name"),
            IndexType.valueOf(rs.getString("index_type")),
            rs.getBoolean("is_unique"),
            rs.getBoolean("is_primary"),
            PredicateParser.parse(rs.getString("index_predicate")),
            parseColumnList(rs.getString("columns")),
            rs.getString("index_definition")
        );
    }

    private Set<ConstraintType> parseConstraintTypes(String constraints) {
        return Stream.of(constraints
                .replaceAll("[\\s{}]", "")
                .split(","))
            .filter(s -> !s.isBlank())
            .map(ConstraintType::valueOf)
            .collect(Collectors.toSet());
    }

    private List<String> parseColumnList(String columns) {
        return Arrays.asList(columns.trim().split(","));
    }

    private record TableColumn(String tableName, Column column) { }
}
