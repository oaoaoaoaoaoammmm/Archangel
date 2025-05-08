package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLOrderBy;
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
public class IndexOrderByCheckerAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String ADVICE = "The ORDER BY clause filters on field '%s' which is not indexed, potentially causing full table scan";
    private static final String ADVICE_COMPOSITE = "The ORDER BY clause filters on fields '%s' which is not indexed, potentially causing full table scan, maybe using composite index";

    private final ArchangelContext context;

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.SELECT;
    }

    @Override
    public boolean shouldWork(SQLRequest sqlRequest) {
        return sqlRequest.getCommandType() == SQLCommandType.SELECT;
    }

    @Override
    public WorkerSignal work(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        if (sqlRequest.getOrderBy() == null) {
            return WorkerSignal.NEXT;
        }
        List<String> orderByFields = sqlRequest.getOrderBy().stream()
            .map(SQLOrderBy::getColumn)
            .filter(this::isSimpleField)
            .map(SQLUtils::extractFieldName)
            .toList();
        if (orderByFields.isEmpty()) {
            return WorkerSignal.NEXT;
        }
        List<String> tables = new ArrayList<>(sqlRequest.getTables());
        if (sqlRequest.getJoins() != null) {
            sqlRequest.getJoins().forEach(join -> tables.add(join.getJoinedTable()));
        }
        tables.stream()
            .map(context::getTable)
            .forEach(table -> analyzeTableIndexes(table, orderByFields, sqlAnalyzeResult));
        return WorkerSignal.NEXT;
    }

    private void analyzeTableIndexes(Table table, List<String> fields, SQLAnalyzeResult result) {
        List<String> unindexedFields = findUnindexedFields(table, fields);
        if (unindexedFields.isEmpty()) {
            return;
        }
        String advice = createAdviceMessage(unindexedFields);
        result.addAdvice(new Advice(advice, AdviceType.INDEX_OPT, Importance.MEDIUM));
    }

    private String createAdviceMessage(List<String> unindexedFields) {
        if (unindexedFields.size() == 1) {
            return String.format(ADVICE, unindexedFields.getFirst());
        }
        return String.format(ADVICE_COMPOSITE, String.join(", ", unindexedFields));
    }
}
