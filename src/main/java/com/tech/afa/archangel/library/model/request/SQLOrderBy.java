package com.tech.afa.archangel.library.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLOrderBy {
    private String column;
    private boolean ascending;

    @Override
    public String toString() {
        return "SQLOrderBy {" +
            "column='" + column + "'" +
            ", direction=" + (ascending ? "ASC" : "DESC") +
            "}";
    }
}
