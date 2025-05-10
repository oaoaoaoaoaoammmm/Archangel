package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.utils.SQLUtils;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class IndexWhereCheckerAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String ADVICE = "The WHERE clause filters on field '%s' which is not indexed on table '%s', potentially causing full table scan.";
    private static final String ADVICE_COMPOSITE = "The WHERE clause filters on fields '%s' which is not indexed on table '%s', potentially causing full table scan, maybe using composite index.";

    private final ArchangelContext context;

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest sqlRequest) {
        List<SQLCommandType> types = List.of(SQLCommandType.SELECT, SQLCommandType.UPDATE, SQLCommandType.DELETE);
        return types.contains(sqlRequest.getCommandType());
    }

    @Override
    public WorkerSignal work(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        if (sqlRequest.getWhereCondition() == null) {
            return WorkerSignal.NEXT;
        }
        List<String> conditionFields = extractSimpleConditions(sqlRequest.getWhereCondition()).stream()
            .map(SQLCondition::getFieldName)
            .map(SQLUtils::extractFieldName)
            .toList();
        if (conditionFields.isEmpty()) {
            return WorkerSignal.NEXT;
        }
        List<String> tables = new ArrayList<>(sqlRequest.getTables());
        if (sqlRequest.getJoins() != null) {
            sqlRequest.getJoins().forEach(join -> tables.add(join.getJoinedTable()));
        }
        tables.stream()
            .map(context::getTable)
            .forEach(table -> analyzeTableIndexes(table, conditionFields, sqlAnalyzeResult));
        return WorkerSignal.NEXT;
    }

    private List<SQLCondition> extractSimpleConditions(SQLCondition condition) {
        List<SQLCondition> simpleConditions = new ArrayList<>();
        traverseConditions(condition, simpleConditions);
        return simpleConditions.stream()
            .filter(this::isSimpleCondition)
            .toList();
    }

    private void analyzeTableIndexes(Table table, List<String> conditions, SQLAnalyzeResult result) {
        List<String> unindexedFields = findUnindexedFields(table, conditions);
        if (unindexedFields.isEmpty()) {
            return;
        }
        String advice = createAdviceMessage(table.getName(), unindexedFields);
        result.addAdvice(new Advice(advice, AdviceType.INDEX_OPT, Importance.MEDIUM));
    }

    private String createAdviceMessage(String tableName, List<String> unindexedFields) {
        if (unindexedFields.size() == 1) {
            return String.format(ADVICE, unindexedFields.getFirst(), tableName);
        }
        return String.format(ADVICE_COMPOSITE, String.join(", ", unindexedFields), tableName);
    }
}
