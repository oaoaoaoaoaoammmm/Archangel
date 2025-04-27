package com.tech.afa.archangel.library.model.table;

import com.tech.afa.archangel.library.model.enums.Condition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Predicate {
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