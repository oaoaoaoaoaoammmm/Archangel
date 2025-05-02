package com.tech.afa.archangel.library.model;

import com.tech.afa.archangel.library.model.stats.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SQLAnalyzeResult {
    private String id;
    private String nativeSql;
    private Statistics statistics;

    @Override
    public String toString() {
        return String.format(
                """
                SQLAnalyzeResult {
                    ID: %s
                    SQL: %s
                    Statistics: %s
                }""",
            id, nativeSql, statistics
        );
    }
}
