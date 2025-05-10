package com.tech.afa.archangel.library.exporter;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogExporter implements Exporter {

    @Override
    public void export(SQLAnalyzeResult result) {
        log.info("Analyze result for sql request with id {}:\n{}", result.getId(), result.toString());
    }
}
