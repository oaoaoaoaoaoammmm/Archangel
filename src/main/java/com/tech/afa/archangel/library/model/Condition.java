package com.tech.afa.archangel.library.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Condition {
    EQUALS("="),
    NOT_EQUALS("!="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
    LIKE("LIKE"),
    ILIKE("ILIKE"),
    BETWEEN("BETWEEN"),
    IN("IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    AND("AND"),
    OR("OR"),
    UNKNOWN("UNKNOWN");

    private final String operator;

    public static Condition fromString(String text) {
        for (Condition condition : Condition.values()) {
            if (condition.operator.equalsIgnoreCase(text)) {
                return condition;
            }
        }
        return Condition.UNKNOWN;
    }

    @Override
    public String toString() {
        return operator;
    }
}
