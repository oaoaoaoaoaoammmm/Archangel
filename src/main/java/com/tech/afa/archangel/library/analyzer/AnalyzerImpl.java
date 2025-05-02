package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.Statistics;
import com.tech.afa.archangel.library.repository.StatisticsRepository;
import com.tech.afa.archangel.library.worker.AnalyzeWorker;
import com.tech.afa.archangel.library.worker.AnalyzeWorkerType;
import com.tech.afa.archangel.library.worker.WorkerStarter;
import com.tech.afa.archangel.library.worker.analyzer.ExecutionPlanAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.IndexGroupByCheckerAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.IndexOrderByCheckerAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.IndexWhereCheckerAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.ManyLinesDetectorAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.StarDetectorAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.SubSelectResearchAnalyzeWorker;
import com.tech.afa.archangel.library.worker.analyzer.TransformConditionAnalyzeWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AnalyzerImpl implements Analyzer {

    private final DataSource dataSource;

    private final ArchangelContext context;

    private final Map<AnalyzeWorkerType, List<AnalyzeWorker<SQLRequest>>> workers;

    public AnalyzerImpl(DataSource dataSource, ArchangelContext context) {
        this.dataSource = dataSource;
        this.context = context;
        this.workers = this.initMap();
    }

    @Override
    public SQLAnalyzeResult analyze(SQLRequest request, Statistics stats) {
        context.put(request.getId(), request, stats);
        AnalyzeWorkerType workerType = AnalyzeWorkerType.valueOf(request.getCommandType().name());
        SQLAnalyzeResult result = new SQLAnalyzeResult(request.getId(), request.getNativeSql(), stats);
        WorkerStarter.startWork(request, result, workers.get(workerType));
        return result;
    }

    private Map<AnalyzeWorkerType, List<AnalyzeWorker<SQLRequest>>> initMap() {
        Map<AnalyzeWorkerType, List<AnalyzeWorker<SQLRequest>>> map = new HashMap<>();
        StatisticsRepository statisticsRepository = new StatisticsRepository(dataSource);
        ExecutionPlanAnalyzeWorker executionPlanAnalyzeWorker = new ExecutionPlanAnalyzeWorker(statisticsRepository);
        ManyLinesDetectorAnalyzeWorker manyLinesDetectorAnalyzeWorker = new ManyLinesDetectorAnalyzeWorker();
        IndexWhereCheckerAnalyzeWorker indexWhereCheckerAnalyzeWorker = new IndexWhereCheckerAnalyzeWorker(context);
        TransformConditionAnalyzeWorker transformConditionAnalyzeWorker = new TransformConditionAnalyzeWorker(context);
        SubSelectResearchAnalyzeWorker subSelectResearchAnalyzeWorker = new SubSelectResearchAnalyzeWorker(map);
        List<AnalyzeWorker<SQLRequest>> general = new ArrayList<>();
        general.add(executionPlanAnalyzeWorker);
        general.add(manyLinesDetectorAnalyzeWorker);
        general.add(indexWhereCheckerAnalyzeWorker);
        general.add(transformConditionAnalyzeWorker);
        general.add(subSelectResearchAnalyzeWorker);
        List<AnalyzeWorker<SQLRequest>> select = new ArrayList<>(general);
        StarDetectorAnalyzeWorker starDetectorAnalyzeWorker = new StarDetectorAnalyzeWorker();
        IndexOrderByCheckerAnalyzeWorker indexOrderByCheckerAnalyzeWorker = new IndexOrderByCheckerAnalyzeWorker(context);
        IndexGroupByCheckerAnalyzeWorker indexGroupByCheckerAnalyzeWorker = new IndexGroupByCheckerAnalyzeWorker(context);
        select.addFirst(starDetectorAnalyzeWorker);
        select.addFirst(indexOrderByCheckerAnalyzeWorker);
        select.addFirst(indexGroupByCheckerAnalyzeWorker);
        List<AnalyzeWorker<SQLRequest>> insert = new ArrayList<>(general);
        List<AnalyzeWorker<SQLRequest>> update = new ArrayList<>(general);
        List<AnalyzeWorker<SQLRequest>> delete = new ArrayList<>(general);
        //System.out.println("General " + general.size());
        //System.out.println("Select " + select.size());
        //System.out.println("Insert " + insert.size());
        //System.out.println("Update " + update.size());
        //System.out.println("Delete " + delete.size());
        //System.out.println("General " + general);
        //System.out.println("Select " + select);
        //System.out.println("Insert " + insert);
        //System.out.println("Update " + update);
        //System.out.println("Delete " + delete);
        map.put(AnalyzeWorkerType.SELECT, select);
        map.put(AnalyzeWorkerType.INSERT, insert);
        map.put(AnalyzeWorkerType.UPDATE, update);
        map.put(AnalyzeWorkerType.DELETE, delete);
        return Map.copyOf(map);
    }
}
