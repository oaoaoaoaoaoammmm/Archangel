package com.tech.afa.archangel.library.schemaloader;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.Table;
import com.tech.afa.archangel.library.repository.SchemaRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ContextLoader {

    private final SchemaRepository schemaRepository;

    public ArchangelContext loadContext(String schema) {
        Map<String, Table> tables = new HashMap<>();
        schemaRepository.loadTables(schema).forEach(table ->
            tables.put(table, new Table(schema, table, null, null))
        );
        schemaRepository.loadColumns(schema).forEach((table, columns) ->
            tables.get(table).setColumns(columns)
        );
        tables.forEach((noop, table) -> schemaRepository.loadIndexes(schema, table.getName())
            .forEach((tableName, indexes) -> tables.get(tableName).setIndexes(indexes)));
        return new ArchangelContext(tables);
    }
}
