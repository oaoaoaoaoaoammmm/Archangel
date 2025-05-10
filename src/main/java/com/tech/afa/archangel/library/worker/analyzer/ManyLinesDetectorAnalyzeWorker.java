package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;

import java.util.List;

public class ManyLinesDetectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final int MAX_LIMIT = 1000;

    private static final String UNBOUNDED_RESULT_ADVICE =
        "Query may perform a full table scan - consider adding WHERE conditions or LIMIT clause to restrict the number of rows processed";
    private static final String LARGE_RESULT_ADVICE =
        "Query may process up to %d rows which could impact performance - consider adding more specific filters or reducing the LIMIT value";

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
            Advice advice = new Advice("", AdviceType.GENERAL_OPT, Importance.MEDIUM);
            if (sqlRequest.getLimit() == null) {
                advice.setAdvice(UNBOUNDED_RESULT_ADVICE);
                sqlAnalyzeResult.addAdvice(advice);
            } else if (sqlRequest.getLimit() >= MAX_LIMIT) {
                advice.setAdvice(LARGE_RESULT_ADVICE.formatted(sqlRequest.getLimit()));
                sqlAnalyzeResult.addAdvice(advice);
            }
        }
        return WorkerSignal.NEXT;
    }
}
