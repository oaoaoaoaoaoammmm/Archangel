package com.tech.afa.archangel.library.processor;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.analyzer.StatisticService;
import com.tech.afa.archangel.library.config.TriggerMode;
import com.tech.afa.archangel.library.exporter.Exporter;
import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import com.tech.afa.archangel.library.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
            System.out.println(sqlRequest);
            RequestStatistics reqStats = statisticService.calculateRequestStatistics(sqlRequest);
            if (sqlRequest.getCommandType() != SQLCommandType.UNKNOWN) {
                boolean isFirstTime = reqStats.getRequestCount() == 1;
                boolean trig = reqStats.getRequestCount() % triggerThreshold == 0 || isFirstTime;
                boolean shouldResetStats = reqStats.getRequestCount() % triggerThreshold == 1;
                List<TableStatistics> tableStats = statisticService.calculateTablesStatistics(sqlRequest, isFirstTime);
                if (triggerMode == TriggerMode.BY_COUNT && trig) {
                    SQLAnalyzeResult sqlAnalyzeResult = analyzer.analyze(sqlRequest, reqStats, tableStats);
                    //if (!isFirstTime) {
                        exporter.export(sqlAnalyzeResult);
                    //}
                } else {
                    // TODO TRIGGER_MODE.BY_TIME... и так далее
                }
                if (shouldResetStats) {
                    reqStats.reset(sqlView.executeTime());
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
