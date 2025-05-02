package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.repository.StatisticsRepository;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutionPlanAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private final StatisticsRepository repository;

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest sqlResult) {
        return true;
    }

    @Override
    public WorkerSignal work(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        String executionPlan;
        if (sqlRequest.getCommandType() == SQLCommandType.SELECT) {
            executionPlan = repository.getExplainAnalyzeBuffers(sqlAnalyzeResult.getNativeSql());
        } else {
            executionPlan = repository.getExplain(sqlAnalyzeResult.getNativeSql());
        }
        sqlAnalyzeResult.setExecutionPlan(executionPlan);
        return WorkerSignal.NEXT;
    }
}
