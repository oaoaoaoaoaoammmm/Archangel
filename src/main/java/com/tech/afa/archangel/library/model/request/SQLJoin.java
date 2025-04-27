package com.tech.afa.archangel.library.model.request;

import com.tech.afa.archangel.library.model.enums.SQLJoinType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLJoin {
    private String joinedTable;
    private SQLCondition condition;
    private SQLJoinType type;
    private SQLRequest subSelect;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SQLJoin {");
        if (joinedTable != null) sb.append("\n  joinedTable='").append(joinedTable).append("'");
        if (type != null) sb.append("\n  type=").append(type);
        if (condition != null) sb.append("\n  condition=").append(condition.toString().replace("\n", "\n  "));
        if (subSelect != null) sb.append("\n  subSelect=").append(subSelect.toString().replace("\n", "\n  "));
        return sb.append("\n}").toString();
    }
}
