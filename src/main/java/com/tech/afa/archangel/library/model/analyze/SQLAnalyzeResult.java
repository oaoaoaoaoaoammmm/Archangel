package com.tech.afa.archangel.library.model.analyze;

import com.tech.afa.archangel.library.model.stats.Statistics;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SQLAnalyzeResult {
    private String id;
    private String nativeSql;
    private Statistics statistics;
    private List<Advice> advices;
    private String executionPlan;

    public SQLAnalyzeResult(String id, String nativeSql, Statistics statistics) {
        this.id = id;
        this.nativeSql = nativeSql;
        this.statistics = statistics;
        this.advices = new ArrayList<>();
        this.executionPlan = "";
    }

    public void addAdvice(Advice advice) {
        this.advices.add(advice);
    }

    @Override
    public String toString() {
        return String.format(
                """
                SQLAnalyzeResult {
                    ID: %s
                    SQL: %s
                    Statistics: %s
                    Advices ->
                %s
                    Execution Plan ->
                %s}""",
            id, nativeSql, statistics, formatAdvices(advices), executionPlan
        );
    }

    private String formatAdvices(List<Advice> advices) {
        if (advices == null || advices.isEmpty()) {
            return "        No advice available";
        }
        StringBuilder sb = new StringBuilder();
        advices.forEach(advice -> sb.append(advice.toString()));
        return sb.toString();
    }
}
