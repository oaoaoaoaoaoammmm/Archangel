package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.model.table.Index;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexStatisticsCollectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String INDEX = "Index";

    private final ArchangelContext context;

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
        if (!sqlAnalyzeResult.getExecutionPlan().contains(INDEX)) {
            return WorkerSignal.NEXT;
        }
        sqlRequest.getTables().stream()
            .map(context::getTable)
            .forEach(table -> {
                TableStatistics stats = table.getStatistics();
                String executionPlan = sqlAnalyzeResult.getExecutionPlan();
                table.getIndexes().stream()
                    .map(Index::getName)
                    .filter(executionPlan::contains)
                    .forEach(stats::addUsableIndex);
            });
        return WorkerSignal.NEXT;
    }
}
