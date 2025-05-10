package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLGroupBy;
import com.tech.afa.archangel.library.model.request.SQLOrderBy;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TransformConditionAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String ADVICE = "The expression %s prevents the index from being used. Change it to use a raw column, or create an index based on a function.";

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest sqlRequest) {
        return true;
    }

    @Override
    public WorkerSignal work(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        List<SQLCondition> conditionsWithBadFormat = new ArrayList<>();
        if (sqlRequest.getJoins() != null) {
            sqlRequest.getJoins().forEach(join -> traverseConditions(join.getCondition(), conditionsWithBadFormat));
        }
        traverseConditions(sqlRequest.getWhereCondition(), conditionsWithBadFormat);
        traverseConditions(sqlRequest.getHavingCondition(), conditionsWithBadFormat);
        conditionsWithBadFormat.stream()
            .map(SQLCondition::getFieldName)
            .filter(field -> !this.isSimpleField(field))
            .filter(Objects::nonNull)
            .forEach(field -> {
                String advice = String.format(ADVICE, field);
                sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.INDEX_OPT, Importance.LOW));
            });
        List<String> fieldsWithBadFormat = new ArrayList<>();
        if (sqlRequest.getOrderBy() != null) {
            sqlRequest.getOrderBy()
                .stream()
                .map(SQLOrderBy::getColumn)
                .filter(Objects::nonNull)
                .filter(column -> !this.isSimpleField(column))
                .forEach(fieldsWithBadFormat::add);
        }
        if (sqlRequest.getGroupBy() != null) {
            sqlRequest.getGroupBy()
                .stream()
                .map(SQLGroupBy::getColumn)
                .filter(Objects::nonNull)
                .filter(column -> !this.isSimpleField(column))
                .forEach(fieldsWithBadFormat::add);
        }
        fieldsWithBadFormat.forEach(field -> {
            String advice = String.format(ADVICE, field);
            sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.INDEX_OPT, Importance.LOW));
        });
        return WorkerSignal.NEXT;
    }
}
