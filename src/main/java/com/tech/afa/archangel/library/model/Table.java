package com.tech.afa.archangel.library.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Table {
    private String schema;
    private String name;
    private List<Column> columns;
    private List<Index> indexes;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table: ").append(schema).append(".").append(name).append("\n");

        sb.append("Columns:\n");
        if (columns.isEmpty()) {
            sb.append("  No columns\n");
        } else {
            columns.forEach(col -> sb.append("  ").append(col.toString()).append("\n"));
        }

        sb.append("Indexes:\n");
        if (indexes.isEmpty()) {
            sb.append("  No indexes\n");
        } else {
            indexes.forEach(idx -> sb.append("  ").append(idx.toString()).append("\n"));
        }

        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    public static class Column {
        private String name;
        private String type;
        private Set<ConstraintType> constraints;

        @Override
        public String toString() {
            return "name: " + name + " | type: " + type +
                (constraints.isEmpty() ? "" : " | constraints: " + constraints.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
        }
    }

    @Data
    @AllArgsConstructor
    public static class Index {
        private String name;
        private IndexType type;
        private boolean unique;
        private boolean primaryKey;
        private Predicate predicate;
        private List<String> fieldNames;
        private String definition;

        @Override
        public String toString() {
            return "name: " + name + " | type: " + type +
                (unique ? " | unique" : "") +
                (primaryKey ? " | primary" : "") +
                " | fields: " + String.join(", ", fieldNames) +
                (predicate != null ? " | condition: " + predicate : "");
        }

        @Data
        @AllArgsConstructor
        public static class Predicate {
            private String fieldName;
            private Condition condition;
            private String value;
            private Condition nextPredicateCondition;
            private Predicate nextPredicate;

            @Override
            public String toString() {
                String result = fieldName + " " + condition + " " + value;
                if (nextPredicate != null) {
                    result += " " + nextPredicateCondition + " " + nextPredicate;
                }
                return result;
            }
        }
    }
}