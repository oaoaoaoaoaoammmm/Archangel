package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.stats.TableStatistics;

import java.util.List;

public interface Analyzer {

    SQLAnalyzeResult analyze(SQLRequest sqlRequest, RequestStatistics reqStats, List<TableStatistics> tableStats);
}
