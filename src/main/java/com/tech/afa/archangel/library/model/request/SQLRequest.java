package com.tech.afa.archangel.library.model.request;

import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLRequest {
    private String id;
    private String nativeSql;
    private SQLCommandType commandType;
    private List<String> tables;
    private List<String> columns;
    private List<SQLJoin> joins;
    private SQLCondition whereCondition;
    private SQLCondition havingCondition;
    private List<SQLOrderBy> orderBy;
    private List<SQLGroupBy> groupBy;
    private Integer limit;
    private List<List<SQLValue>> values;

    private Map<String, String> aliasToExpression;

    public SQLRequest(String id, String nativeSql, SQLCommandType commandType) {
        this.id = id;
        this.nativeSql = nativeSql;
        this.commandType = commandType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SQLRequest {\n");
        if (id != null) sb.append("  id='").append(id).append("'\n");
        if (nativeSql != null) {
            String processedSql = nativeSql.replace("\n", " ").replaceAll("\\s+", " ").trim();
            sb.append("  nativeSql='").append(processedSql).append("'\n");
        }
        if (commandType != null) sb.append("  commandType=").append(commandType).append("\n");
        if (tables != null && !tables.isEmpty()) sb.append("  tables=").append(tables).append("\n");
        if (columns != null && !columns.isEmpty()) sb.append("  columns=").append(columns).append("\n");
        if (joins != null && !joins.isEmpty()) {
            sb.append("  joins=[\n");
            joins.forEach(join -> sb.append("    ").append(join.toString().replace("\n", "\n    ")).append(",\n"));
            sb.append("  ]\n");
        }
        if (whereCondition != null) sb.append("  where=").append(whereCondition.toString().replace("\n", "\n  ")).append("\n");
        if (havingCondition != null) sb.append("  having=").append(havingCondition.toString().replace("\n", "\n  ")).append("\n");
        if (orderBy != null && !orderBy.isEmpty()) sb.append("  orderBy=").append(orderBy).append("\n");
        if (groupBy != null && !groupBy.isEmpty()) sb.append("  groupBy=").append(groupBy).append("\n");
        if (limit != null) sb.append("  limit=").append(limit).append("\n");
        if (values != null && !values.isEmpty()) {
            sb.append("  values=[\n");
            values.forEach(list -> {
                sb.append("    [\n");
                list.forEach(val -> sb.append("      ").append(val.toString().replace("\n", "\n      ")).append(",\n"));
                sb.append("    ],\n");
            });
            sb.append("  ]\n");
        }
        return sb.append("}").toString();
    }
}

