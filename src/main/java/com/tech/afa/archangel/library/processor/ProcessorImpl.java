package com.tech.afa.archangel.library.processor;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.exporter.Exporter;
import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.SQLRequest;
import com.tech.afa.archangel.library.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

    private final Parser parser;

    private final Exporter exporter;

    private final Analyzer analyzer;

    @Override
    public void process(String sql) {
        SQLRequest sqlRequest = parser.parse(sql);
        SQLAnalyzeResult sqlAnalyzeResult = analyzer.analyze(sqlRequest);
        exporter.export(sqlAnalyzeResult);
    }
}
