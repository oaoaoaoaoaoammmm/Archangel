package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
public class AnalyzerImpl implements Analyzer {

    private final DataSource dataSource;

    private final ArchangelContext context;

    @Override
    public SQLAnalyzeResult analyze(SQLRequest request, Statistics stats) {
        context.put(request.getId(), request, stats);
        return new SQLAnalyzeResult(request.getId(), request.getNativeSql(), stats);
    }
}
