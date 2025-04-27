package com.tech.afa.archangel.library.processor;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.exporter.Exporter;
import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.SQLRequestView;
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
    public void processing(SQLRequestView sqlView) {
        try {
            System.out.println(sqlView.sql());
            SQLRequest sqlRequest = parser.parse(sqlView);
            System.out.println(sqlRequest);
            SQLAnalyzeResult sqlAnalyzeResult = analyzer.analyze(sqlRequest);
            exporter.export(sqlAnalyzeResult);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
