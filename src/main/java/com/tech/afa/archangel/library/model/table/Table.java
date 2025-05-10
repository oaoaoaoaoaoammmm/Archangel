package com.tech.afa.archangel.library.model.table;

import com.tech.afa.archangel.library.model.stats.TableStatistics;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Table {
    private String schema;
    private String name;
    private List<Column> columns;
    private List<Index> indexes;

    private TableStatistics statistics;

    public Table(String schema, String name) {
        this.schema = schema;
        this.name = name;
        this.statistics = new TableStatistics(name);
    }

    public boolean hasColumn(String columnName) {
        return getColumns().stream()
            .anyMatch(col -> col.getName().equals(columnName));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table: ").append(schema).append(".").append(name).append("\n");
        sb.append("Columns:\n");
        if (columns.isEmpty()) {
            sb.append("  No columns\n");
        } else {
            columns.forEach(col -> sb.append("  ").append(col.toString()).append("\n"));
        }
        sb.append("Indexes:\n");
        if (indexes.isEmpty()) {
            sb.append("  No indexes\n");
        } else {
            indexes.forEach(idx -> sb.append("  ").append(idx.toString()).append("\n"));
        }
        return sb.toString();
    }
}