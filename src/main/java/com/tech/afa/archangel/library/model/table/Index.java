package com.tech.afa.archangel.library.model.table;

import com.tech.afa.archangel.library.model.enums.IndexType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Index {
    private String name;
    private IndexType type;
    private boolean unique;
    private boolean primaryKey;
    private Predicate predicate;
    private List<String> fieldNames;
    private String definition;

    public boolean hasField(String fieldName) {
        return getFieldNames().stream()
            .anyMatch(col -> col.equals(fieldName));
    }

    @Override
    public String toString() {
        return "name: " + name + " | type: " + type +
            (unique ? " | unique" : "") +
            (primaryKey ? " | primary" : "") +
            " | fields: " + String.join(", ", fieldNames) +
            (predicate != null ? " | condition: " + predicate : "");
    }
}