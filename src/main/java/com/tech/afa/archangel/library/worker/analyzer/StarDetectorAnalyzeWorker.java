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

public class StarDetectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String WILDCARD_USAGE_ADVICE =
        "Using '*' to select all columns may impact performance - explicitly specify only required columns.";

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
        if (sqlRequest.getColumns().getFirst().equals("*")) {
            Advice advice = new Advice(WILDCARD_USAGE_ADVICE, AdviceType.GENERAL_OPT, Importance.MEDIUM);
            sqlAnalyzeResult.addAdvice(advice);
        }
        return WorkerSignal.NEXT;
    }
}
