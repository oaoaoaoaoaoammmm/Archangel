package com.tech.afa.archangel.library.model.enums;

import lombok.AllArgsConstructor;

import java.util.Set;

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

    private static final Set<Condition> simples = Set.of(EQUALS, NOT_EQUALS, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL);

    public static Condition fromString(String text) {
        for (Condition condition : Condition.values()) {
            if (condition.operator.equalsIgnoreCase(text)) {
                return condition;
            }
        }
        return Condition.UNKNOWN;
    }

    public boolean isSimple() {
        return simples.contains(this);
    }

    @Override
    public String toString() {
        return operator;
    }
}
