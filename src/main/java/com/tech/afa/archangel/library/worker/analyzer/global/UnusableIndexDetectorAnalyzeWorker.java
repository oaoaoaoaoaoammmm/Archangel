package com.tech.afa.archangel.library.worker.analyzer.global;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.Advice;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.model.table.Column;
import com.tech.afa.archangel.library.model.table.Index;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class UnusableIndexDetectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String UNUSABLE_INDEX_ADVICE = "Table %s has unused index(s) - %s";

    private final ArchangelContext context;

    @Override
    public AnalyzeWorkerType getAnalyzeWorkerType() {
        return AnalyzeWorkerType.GENERAL;
    }

    @Override
    public boolean shouldWork(SQLRequest sqlRequest) {
        return sqlRequest.getCommandType() == SQLCommandType.SELECT;
    }

    @Override
    public WorkerSignal work(SQLRequest sqlRequest, SQLAnalyzeResult sqlAnalyzeResult) {
        sqlRequest.getTables().stream()
            .map(context::getTable)
            .forEach(table -> {
                TableStatistics stats = table.getStatistics();
                List<String> unusedIndexNames = new ArrayList<>();
                table.getIndexes().stream()
                    .map(Index::getName)
                    .filter(indexName -> !stats.hasUsableIndex(indexName))
                    .forEach(unusedIndexNames::add);
                if (!unusedIndexNames.isEmpty()) {
                    String advice = String.format(UNUSABLE_INDEX_ADVICE, table.getName(), unusedIndexNames);
                    sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.GENERAL_OPT, Importance.CRITICAL));
                }
            });
        return WorkerSignal.NEXT;
    }
}
