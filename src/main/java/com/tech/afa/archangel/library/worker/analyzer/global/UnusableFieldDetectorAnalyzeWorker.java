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
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerSignal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UnusableFieldDetectorAnalyzeWorker implements AnalyzeWorker<SQLRequest> {

    private static final String STAR = "*";
    private static final String UNUSABLE_FIELD_ADVICE = "Table %s has unused field(s) - %s";

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
        if (sqlRequest.getColumns().getFirst().equals(STAR)) {
            return WorkerSignal.NEXT;
        }
        sqlRequest.getTables().stream()
            .map(context::getTable)
            .filter(table -> !table.getStatistics().hasUsableField(STAR))
            .forEach(table -> {
                TableStatistics stats = table.getStatistics();
                List<String> unusedFiledNames = new ArrayList<>();
                table.getColumns().stream()
                    .map(Column::getName)
                    .filter(columnName -> !stats.hasUsableField(columnName))
                    .forEach(unusedFiledNames::add);
                if (!unusedFiledNames.isEmpty()) {
                    String advice = String.format(UNUSABLE_FIELD_ADVICE, table.getName(), unusedFiledNames);
                    sqlAnalyzeResult.addAdvice(new Advice(advice, AdviceType.GENERAL_OPT, Importance.CRITICAL));
                }
            });
        return WorkerSignal.NEXT;
    }
}
