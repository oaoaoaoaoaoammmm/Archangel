package com.tech.afa.archangel.library.exporter;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;

public interface Exporter {

    void export(SQLAnalyzeResult result);
}
