package com.tech.afa.archangel.library.model.table;

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