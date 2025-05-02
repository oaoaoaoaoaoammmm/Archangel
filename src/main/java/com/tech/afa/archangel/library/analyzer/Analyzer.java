package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.Statistics;

public interface Analyzer {

    SQLAnalyzeResult analyze(SQLRequest sqlRequest, Statistics stats);
}
