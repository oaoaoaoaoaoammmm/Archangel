package com.tech.afa.archangel.library.analyzer;

import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.model.SQLAnalyzeResult;
import com.tech.afa.archangel.library.model.SQLRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
public class AnalyzerImpl implements Analyzer {

    private final DataSource dataSource;

    private final ArchangelContext context;

    @Override
    public SQLAnalyzeResult analyze(SQLRequest sqlRequest) {
        return new SQLAnalyzeResult(sqlRequest.getNativeSql());
    }
}
