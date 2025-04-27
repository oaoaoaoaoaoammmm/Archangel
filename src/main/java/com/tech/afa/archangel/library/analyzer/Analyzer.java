package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;

public interface Analyzer {

    SQLAnalyzeResult analyze(SQLRequest sqlRequest);
}
