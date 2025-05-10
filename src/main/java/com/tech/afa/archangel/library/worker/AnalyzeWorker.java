package com.tech.afa.archangel.library.worker;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.table.Column;
import com.tech.afa.archangel.library.model.table.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public interface AnalyzeWorker<T> extends Worker<T, SQLAnalyzeResult> {

    AnalyzeWorkerType getAnalyzeWorkerType();

    default boolean isSimpleCondition(SQLCondition condition) {
        return (condition.getFieldName() != null && isSimpleField(condition.getFieldName()))
            && condition.getCondition().isSimple()
            && condition.getSubSelect() == null;
    }

    default boolean isSimpleField(String field) {
        if (field == null || field.isEmpty()) {
            return false;
        } else {
            // field1
            // table.field
            // schema3.table.field
            return field.matches("^([a-zA-Z_][a-zA-Z0-9_]*\\.)?([a-zA-Z_][a-zA-Z0-9_]*\\.)?[a-zA-Z_][a-zA-Z0-9_]*$");
        }
    }

    default void traverseConditions(SQLCondition current, List<SQLCondition> accumulator) {
        if (current == null) {
            return;
        }
        accumulator.add(current);
        Optional.ofNullable(current.getAndConditions())
            .ifPresent(conds -> conds.forEach(c -> traverseConditions(c, accumulator)));
        Optional.ofNullable(current.getOrConditions())
            .ifPresent(conds -> conds.forEach(c -> traverseConditions(c, accumulator)));
    }

    default List<String> findUnindexedFields(Table table, List<String> fields) {
        List<String> tableFields = table.getColumns().stream()
            .map(Column::getName)
            .toList();
        List<String> indexedFields = table.getIndexes().stream()
            .flatMap(index -> index.getFieldNames().stream())
            .toList();
        return fields.stream()
            .filter(tableFields::contains)
            .filter(field -> !indexedFields.contains(field))
            .toList();
    }
}
