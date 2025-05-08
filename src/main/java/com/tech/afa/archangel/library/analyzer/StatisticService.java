package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLJoin;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.utils.SQLUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StatisticService {

    private final ArchangelContext context;

    public RequestStatistics calculateRequestStatistics(SQLRequest request) {
        RequestStatistics stats = context.getStatistics(request.getId());
        stats = stats == null ? context.put(request.getId(), request, new RequestStatistics()).second() : stats;
        stats.incrementRequestCountAndGet();
        long executeTime = request.getExecuteTime();
        long maxExecuteTime = Math.max(stats.getMaxExecuteTimeMs(), executeTime);
        long minExecuteTime = Math.min(stats.getMinExecuteTimeMs(), executeTime);
        long avgExecuteTime = calcAverageValByIncrementalMethod(stats.getRequestCount(), stats.getAvgExecuteTimeMs(), executeTime);
        stats.setMaxExecuteTimeMs(maxExecuteTime);
        stats.setMinExecuteTimeMs(minExecuteTime);
        stats.setAvgExecuteTimeMs(avgExecuteTime);
        return stats;
    }

    public List<TableStatistics> calculateTablesStatistics(SQLRequest request, boolean isFirstTime) {
        return request.getTables()
            .stream()
            .map(tableName -> {
                Table table = context.getTable(tableName);
                TableStatistics stats = table.getStatistics();
                stats.incrementRequestCountAndGet();
                if (isFirstTime) {
                    getAllFieldNameFromSQLRequest(request, table).forEach(stats::addUsableField);
                }
                return stats;
            })
            .toList();
    }

    private long calcAverageValByIncrementalMethod(long count, long average, long last) {
        return (average * (count - 1) + last) / count;
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
                if (fieldName != null) fieldNames.add(fieldName);
            }
        }
        if (request.getHavingCondition() != null) {
            SQLCondition havingCondition = request.getHavingCondition();
            if (isDefaultCondition(havingCondition) || isConditionWithFieldName(havingCondition)) {
                String fieldName = getFieldNameFromSQLConditionByTableName(havingCondition, table);
                if (fieldName != null) fieldNames.add(fieldName);
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
