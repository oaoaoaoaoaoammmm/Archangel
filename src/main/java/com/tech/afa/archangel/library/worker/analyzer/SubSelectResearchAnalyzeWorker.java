package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import com.tech.afa.archangel.library.worker.WorkerStarter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class SubSelectResearchAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private final Map<AnalyzeWorkerType, List<AnalyzeWorker<SQLRequest>>> workers;

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
        if (sqlRequest.getJoins() != null) {
            sqlRequest.getJoins().forEach(join -> {
                if (join.getSubSelect() != null) {
                    startSubSelectAnalyze(join.getSubSelect(), sqlAnalyzeResult);
                }
                if (join.getCondition() != null) {
                    foreachSubSelectInCondition(join.getCondition(), sqlAnalyzeResult);
                }
            });
        }
        if (sqlRequest.getWhereCondition() != null) {
            foreachSubSelectInCondition(sqlRequest.getWhereCondition(), sqlAnalyzeResult);
        }
        if (sqlRequest.getHavingCondition() != null) {
            foreachSubSelectInCondition(sqlRequest.getHavingCondition(), sqlAnalyzeResult);
        }
        if (sqlRequest.getValues() != null) {
            sqlRequest.getValues().stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .forEach(value -> startSubSelectAnalyze(value.getSelectRequest(), sqlAnalyzeResult));
        }
        return WorkerSignal.NEXT;
    }

    private void foreachSubSelectInCondition(SQLCondition condition, SQLAnalyzeResult sqlAnalyzeResult) {
        List<SQLCondition> conditions = new ArrayList<>();
        traverseConditions(condition, conditions);
        conditions.stream()
            .map(SQLCondition::getSubSelect)
            .filter(Objects::nonNull)
            .forEach(subSelect -> startSubSelectAnalyze(subSelect, sqlAnalyzeResult));
    }

    private void startSubSelectAnalyze(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        System.out.println(sqlRequest);
        SQLCommandType commandType = sqlRequest.getCommandType();
        AnalyzeWorkerType workerType = AnalyzeWorkerType.valueOf(commandType.name());
        WorkerStarter.startWork(sqlRequest, sqlAnalyzeResult, workers.get(workerType));
    }
}
