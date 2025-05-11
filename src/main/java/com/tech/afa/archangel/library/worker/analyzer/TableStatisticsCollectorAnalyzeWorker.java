package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLJoin;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.utils.SQLUtils;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TableStatisticsCollectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private final ArchangelContext context;

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest request) {
        return true;
    }

    @Override
    public WorkerSignal work(SQLRequest request, SQLAnalyzeResult sqlAnalyzeResult) {
        if (sqlAnalyzeResult.getRequestStatistics().getRequestCount() == 1) {
            request.getTables().stream()
                .map(context::getTable)
                .forEach(table -> {
                    TableStatistics stats = table.getStatistics();
                    getAllFieldNameFromSQLRequest(request, table).forEach(stats::addUsableField);
                });
        }
        return WorkerSignal.NEXT;
    }

    private List<String> getAllFieldNameFromSQLRequest(SQLRequest request, Table table) {
        List<String> fieldNames = request.getColumns().stream()
            .map(SQLUtils::extractFieldName)
            .filter(table::hasColumn)
            .collect(Collectors.toCollection(ArrayList::new));
        if (request.getColumns().getFirst().equals("*")) {
            fieldNames.add("*");
        }
        if (request.getJoins() != null) {
            accumulateFieldNamesFromJoin(request, table, this::isDefaultJoin, fieldNames);
            accumulateFieldNamesFromJoin(request, table, this::isJoinWithFieldName, fieldNames);
        }
        if (request.getWhereCondition() != null) {
            SQLCondition whereCondition = request.getWhereCondition();
            if (isDefaultCondition(whereCondition) || isConditionWithFieldName(whereCondition)) {
                String fieldName = getFieldNameFromSQLConditionByTableName(whereCondition, table);
                if (fieldName != null) {
                    fieldNames.add(fieldName);
                }
            }
        }
        if (request.getHavingCondition() != null) {
            SQLCondition havingCondition = request.getHavingCondition();
            if (isDefaultCondition(havingCondition) || isConditionWithFieldName(havingCondition)) {
                String fieldName = getFieldNameFromSQLConditionByTableName(havingCondition, table);
                if (fieldName != null) {
                    fieldNames.add(fieldName);
                }
            }
        }

        return fieldNames;
    }

    private void accumulateFieldNamesFromJoin(SQLRequest request, Table table, Predicate<SQLJoin> predicate, List<String> fieldNames) {
        request.getJoins().stream()
            .filter(predicate)
            .map(SQLJoin::getCondition)
            .map(cond -> getFieldNameFromSQLConditionByTableName(cond, table))
            .filter(Objects::nonNull)
            .forEach(fieldNames::add);
    }

    private boolean isDefaultJoin(SQLJoin join) {
        return isDefaultCondition(join.getCondition()) && join.getSubSelect() == null;
    }

    private boolean isJoinWithFieldName(SQLJoin join) {
        return isConditionWithFieldName(join.getCondition()) && join.getSubSelect() != null;
    }

    private boolean isDefaultCondition(SQLCondition cond) {
        return (cond != null && cond.getFieldName() != null && cond.getCondition() != null && cond.getValue() != null);
    }

    private boolean isConditionWithFieldName(SQLCondition cond) {
        return (cond != null && cond.getFieldName() != null && cond.getCondition() != null && cond.getValue() == null && cond.getSubSelect() != null);
    }

    private String getFieldNameFromSQLConditionByTableName(SQLCondition condition, Table table) {
        String fieldName = SQLUtils.extractFieldName(condition.getFieldName());
        if (table.hasColumn(fieldName)) {
            return fieldName;
        }
        String fieldNameFromValue = SQLUtils.extractFieldName(condition.getValue());
        if (table.hasColumn(fieldNameFromValue)) {
            return fieldNameFromValue;
        }
        return null;
    }
}
