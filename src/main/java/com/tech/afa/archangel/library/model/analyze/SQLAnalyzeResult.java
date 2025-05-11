package com.tech.afa.archangel.library.model.analyze;

import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.stats.TableStatistics;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SQLAnalyzeResult {
    private String id;
    private String nativeSql;
    private List<Advice> advices;
    private String executionPlan;
    private RequestStatistics requestStatistics;
    private List<TableStatistics> tableStatistics;

    public SQLAnalyzeResult(
        String id,
        String nativeSql,
        RequestStatistics requestStatistics,
        List<TableStatistics> tableStatistics
    ) {
        this.id = id;
        this.nativeSql = nativeSql;
        this.advices = new ArrayList<>();
        this.executionPlan = "";
        this.requestStatistics = requestStatistics;
        this.tableStatistics = tableStatistics;
    }

    public void addAdvice(Advice advice) {
        this.advices.add(advice);
    }

    @Override
    public String toString() {
        return String.format(
            """
                Tables Statistics: %s
                SQLAnalyzeResult {
                    ID: %s
                    SQL: %s
                    Request Statistics: %s
                    Advices: %s
                    Execution Plan: %s
                }""",
            formatTableStatistics(tableStatistics), id,
            nativeSql.replaceAll("\\s+", " "),
            requestStatistics, formatAdvices(advices), formatExecutionPlan(executionPlan)
        );
    }

    private String formatTableStatistics(List<TableStatistics> stats) {
        if (stats == null || stats.isEmpty()) {
            return "No table statistics available";
        }
        StringBuilder sb = new StringBuilder();
        stats.forEach(stat -> sb.append(stat.toString()));
        return sb.toString();
    }

    private String formatAdvices(List<Advice> advices) {
        if (advices == null || advices.isEmpty()) {
            return "No advice available";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        advices.forEach(advice -> sb.append("        ").append(advice.toString()));
        sb.append("    }");
        return sb.toString();
    }

    private String formatExecutionPlan(String executionPlan) {
        if (executionPlan == null || executionPlan.isEmpty()) {
            return "No execution plan available";
        }
        String formattedPlan = executionPlan.lines()
            .map(line -> "        " + line)
            .collect(Collectors.joining("\n"));
        return "{\n" + formattedPlan + "\n    }";
    }
}
