package com.tech.afa.archangel.library.exporter;

import com.tech.afa.archangel.library.model.analyze.SQLAnalyzeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LogExporter implements Exporter {

    @Override
    public void export(SQLAnalyzeResult result) {
        System.out.println(result.toString());
    }
}
