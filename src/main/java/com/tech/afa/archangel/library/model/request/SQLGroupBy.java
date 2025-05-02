package com.tech.afa.archangel.library.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLGroupBy {
    private String column;

    @Override
    public String toString() {
        return "SQLGroupBy {" +
            (column != null ? "column='" + column + "'" : "") +
            "}";
    }
}