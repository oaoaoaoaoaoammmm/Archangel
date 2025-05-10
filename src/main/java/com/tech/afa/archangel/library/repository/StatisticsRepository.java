package com.tech.afa.archangel.library.repository;

import javax.sql.DataSource;

public class StatisticsRepository extends Repository {

    private static final String EXPLAIN_PREFIX = "EXPLAIN ";
    private static final String EXPLAIN_ANALYZE_PREFIX = "EXPLAIN ANALYZE ";
    private static final String EXPLAIN_ANALYZE_BUFFERS_PREFIX = "EXPLAIN (ANALYZE,BUFFERS) ";
    //"EXPLAIN (ANALYZE,BUFFERS,FORMAT JSON) "
    //"EXPLAIN (ANALYZE,BUFFERS,VERBOSE,SETTINGS,WAL) "

    public StatisticsRepository(DataSource dataSource) {
        super(dataSource);
    }

    public String getExplain(String sql) {
        String concatSql = EXPLAIN_PREFIX + sql;
        return executeQuery(concatSql);
    }

    public String getExplainAnalyze(String sql) {
        String concatSql = EXPLAIN_ANALYZE_PREFIX + sql;
        return executeQuery(concatSql);
    }

    public String getExplainAnalyzeBuffers(String sql) {
        String concatSql = EXPLAIN_ANALYZE_BUFFERS_PREFIX + sql;
        return executeQuery(concatSql);
    }
}
