package com.tech.afa.archangel.library.worker.analyzer;

import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.enums.SQLJoinType;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;

public class LargeJoinDetectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String ADVICE_LARGE_JOINS = "Detected '%s' joining table '%s' may cause performance loss.";
    private static final String ADVICE_JOIN_WITHOUT_CONDITION = "Detected '%s' joining table '%s' without condition may cause performance loss.";

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest request) {
        return request.getCommandType() == SQLCommandType.SELECT;
    }

    @Override
    public WorkerSignal work(SQLRequest request, SQLAnalyzeResult sqlAnalyzeResult) {
        if (request.getJoins() == null || request.getJoins().isEmpty()) {
            return WorkerSignal.NEXT;
        }
        request.getJoins().stream()
            .filter(join -> join.getType() != SQLJoinType.UNKNOWN)
            .forEach(join -> {
            if (join.getType() == SQLJoinType.FULL || join.getType() == SQLJoinType.CROSS) {
                String advice = ADVICE_LARGE_JOINS.formatted(join.getType().name(), join.getJoinedTable());
                sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.STRUCT_OPT, Importance.LOW));
            } else if (join.getCondition() == null) {
                String advice = ADVICE_JOIN_WITHOUT_CONDITION.formatted(join.getType().name(), join.getJoinedTable());
                sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.STRUCT_OPT, Importance.MEDIUM));
            }
        });
        return null;
    }
}
