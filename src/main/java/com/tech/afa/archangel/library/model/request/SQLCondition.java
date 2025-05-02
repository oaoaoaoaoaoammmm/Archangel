package com.tech.afa.archangel.library.model.request;

import com.tech.afa.archangel.library.model.enums.Condition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLCondition {
    private String fieldName;
    private Condition condition;
    private String value;
    private String expression;
    private List<SQLCondition> andConditions;
    private List<SQLCondition> orConditions;
    private SQLRequest subSelect;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SQLCondition {");
        if (fieldName != null && condition != null && value != null) sb.append("\n  ").append(fieldName).append(" ").append(condition).append(" ").append(value);
        if (expression != null) sb.append("\n  expression='").append(expression).append("'");
        if (andConditions != null && !andConditions.isEmpty()) {
            sb.append("\n  and=[");
            andConditions.forEach(c -> sb.append("\n    ").append(c.toString().replace("\n", "\n    ")));
            sb.append("\n  ]");
        }
        if (orConditions != null && !orConditions.isEmpty()) {
            sb.append("\n  or=[");
            orConditions.forEach(c -> sb.append("\n    ").append(c.toString().replace("\n", "\n    ")));
            sb.append("\n  ]");
        }
        if (subSelect != null) sb.append("\n  subSelect=").append(subSelect.toString().replace("\n", "\n  "));
        return sb.append("\n}").toString();
    }
}