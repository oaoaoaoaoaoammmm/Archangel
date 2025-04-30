package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.Statistics;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatisticService {

    private final ArchangelContext context;

    public Statistics calculateStatistics(SQLRequest request) {
        Statistics stats = context.getStatistics(request.getId());
        stats = stats == null ? context.put(request.getId(), request, new Statistics()).second() : stats;
        stats.incrementRequestCount();
        long executeTime = request.getExecuteTime();
        long maxExecuteTime = Math.max(stats.getMaxExecuteTimeMs(), executeTime);
        long minExecuteTime = Math.min(stats.getMinExecuteTimeMs(), executeTime);
        long avgExecuteTime = calcAverageValByIncrementalMethod(stats.getRequestCount(), stats.getAvgExecuteTimeMs(), executeTime);
        stats.setMaxExecuteTimeMs(maxExecuteTime);
        stats.setMinExecuteTimeMs(minExecuteTime);
        stats.setAvgExecuteTimeMs(avgExecuteTime);
        return stats;
    }

    private long calcAverageValByIncrementalMethod(long count, long average, long last) {
        return (average * (count - 1) + last) / count;
    }
}
