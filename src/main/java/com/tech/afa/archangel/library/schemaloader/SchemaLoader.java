package com.tech.afa.archangel.library.schemaloader;

import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.repository.SchemaRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SchemaLoader {

    private final String schemaName;

    private final SchemaRepository schemaRepository;

    public Map<String, Table> loadSchema() {
        Map<String, Table> tables = new HashMap<>();
        schemaRepository.loadTables(schemaName).forEach(table ->
            tables.put(table, new Table(schemaName, table))
        );
        schemaRepository.loadColumns(schemaName).forEach((table, columns) ->
            tables.get(table).setColumns(columns)
        );
        tables.forEach((noop, table) ->
            schemaRepository.loadIndexes(schemaName, table.getName())
                .forEach((tableName, indexes) -> tables.get(tableName).setIndexes(indexes)));
        return tables;
    }
}
