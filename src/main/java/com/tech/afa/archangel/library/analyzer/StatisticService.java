package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLJoin;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.utils.SQLUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StatisticService {

    private final ArchangelContext context;

    public RequestStatistics calculateRequestStatistics(SQLRequest request) {
        RequestStatistics stats = context.getStatistics(request.getId());
        stats = stats == null ? context.put(request.getId(), request, new RequestStatistics()).second() : stats;
        stats.incrementRequestCountAndGet();
        long executeTime = request.getExecuteTime();
        long maxExecuteTime = Math.max(stats.getMaxExecuteTimeMs(), executeTime);
        long minExecuteTime = Math.min(stats.getMinExecuteTimeMs(), executeTime);
        long avgExecuteTime = calcAverageValByIncrementalMethod(stats.getRequestCount(), stats.getAvgExecuteTimeMs(), executeTime);
        stats.setMaxExecuteTimeMs(maxExecuteTime);
        stats.setMinExecuteTimeMs(minExecuteTime);
        stats.setAvgExecuteTimeMs(avgExecuteTime);
        return stats;
    }

    public List<TableStatistics> calculateTablesStatistics(SQLRequest request) {
        return request.getTables()
            .stream()
            .map(context::getTable)
            .map(Table::getStatistics)
            .peek(TableStatistics::incrementRequestCountAndGet)
            .toList();
    }

    private long calcAverageValByIncrementalMethod(long count, long average, long last) {
        return (average * (count - 1) + last) / count;
    }
}
