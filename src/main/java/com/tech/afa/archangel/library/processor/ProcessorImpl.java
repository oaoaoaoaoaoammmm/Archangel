package com.tech.afa.archangel.library.processor;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.analyzer.StatisticService;
import com.tech.afa.archangel.library.config.TriggerMode;
import com.tech.afa.archangel.library.exporter.Exporter;
import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.model.stats.Statistics;
import com.tech.afa.archangel.library.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

    private final int triggerThreshold;
    private final TriggerMode triggerMode;

    private final Parser parser;
    private final Exporter exporter;
    private final Analyzer analyzer;
    private final StatisticService statisticService;

    @Override
    public void processing(SQLRequestView sqlView) {
        try {
            SQLRequest sqlRequest = parser.parse(sqlView);
            sqlRequest.setExecuteTime(sqlView.executeTime());
            Statistics stats = statisticService.calculateStatistics(sqlRequest);
            boolean trig = stats.getRequestCount() % triggerThreshold == 0;
            boolean shouldResetStats = stats.getRequestCount() % triggerThreshold == 1;
            if (triggerMode == TriggerMode.BY_COUNT && trig) {
                SQLAnalyzeResult sqlAnalyzeResult = analyzer.analyze(sqlRequest, stats);
                exporter.export(sqlAnalyzeResult);
            } else {
                // TODO TRIGGER_MODE.BY_TIME... и так далее
            }
            if (shouldResetStats) {
                stats.reset(sqlView.executeTime());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
