package com.tech.afa.archangel.library.utils;

import com.tech.afa.archangel.library.model.Condition;
import com.tech.afa.archangel.library.model.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.NONE)
public class PredicateParser {

    public static Table.Index.Predicate parse(String predicateExpression) {
        if (predicateExpression == null || predicateExpression.trim().isEmpty()) {
            return null;
        }
        String normalized = normalizeExpression(predicateExpression);
        Table.Index.Predicate complex = parseComplexCondition(normalized);
        if (complex != null) {
            return complex;
        }
        Table.Index.Predicate nullCheck = parseNullCheck(normalized);
        if (nullCheck != null) {
            return nullCheck;
        }
        Table.Index.Predicate compound = parseCompound(normalized);
        if (compound != null) {
            return compound;
        }
        Table.Index.Predicate between = parseBetween(normalized);
        if (between != null) {
            return between;
        }
        Table.Index.Predicate in = parseIn(normalized);
        if (in != null) {
            return in;
        }
        return parseSimple(normalized);
    }

    private static String normalizeExpression(String expr) {
        expr = expr.trim();
        while (hasOuterParentheses(expr)) {
            expr = expr.substring(1, expr.length() - 1).trim();
        }
        return expr;
    }

    private static boolean hasOuterParentheses(String expr) {
        if (expr.startsWith("(") && expr.endsWith(")")) {
            int open = 1;
            for (int i = 1; i < expr.length() - 1; i++) {
                char c = expr.charAt(i);
                if (c == '(') {
                    open++;
                } else if (c == ')') {
                    open--;
                }
                if (open == 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static Table.Index.Predicate parseComplexCondition(String expr) {
        Pattern pattern = Pattern.compile(
            "^\\s*\\(\\s*([^)]+)\\s*\\)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(expr);
        if (matcher.matches()) {
            return parse(matcher.group(1));
        }
        return null;
    }

    private static Table.Index.Predicate parseNullCheck(String expr) {
        Pattern pattern = Pattern.compile(
            "^([\\w_]+)\\s+(IS\\s+(NOT\\s+)?NULL)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(expr);
        if (matcher.matches()) {
            boolean isNotNull = matcher.group(2) != null;
            return new Table.Index.Predicate(
                matcher.group(1),
                isNotNull ? Condition.IS_NOT_NULL : Condition.IS_NULL,
                null,
                null,
                null
            );
        }
        return null;
    }

    private static Table.Index.Predicate parseCompound(String expr) {
        int andPos = findOperatorPosition(expr, "AND");
        if (andPos > 0) {
            return createCompoundPredicate(expr, andPos, 3, Condition.AND);
        }
        int orPos = findOperatorPosition(expr, "OR");
        if (orPos > 0) {
            return createCompoundPredicate(expr, orPos, 2, Condition.OR);
        }
        return null;
    }

    private static int findOperatorPosition(String expr, String op) {
        int level = 0;
        for (int i = 0; i < expr.length() - op.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') {
                level++;
            } else if (c == ')') {
                level--;
            }
            if (level == 0 && expr.regionMatches(true, i, op, 0, op.length())) {
                boolean before = i == 0 || Character.isWhitespace(expr.charAt(i - 1));
                boolean after = i + op.length() == expr.length() ||
                    Character.isWhitespace(expr.charAt(i + op.length()));
                if (before && after) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static Table.Index.Predicate createCompoundPredicate(String expr, int opPos, int opLength, Condition condition) {
        String left = expr.substring(0, opPos).trim();
        String right = expr.substring(opPos + opLength).trim();
        Table.Index.Predicate leftPred = parse(left);
        Table.Index.Predicate rightPred = parse(right);
        if (leftPred != null && rightPred != null) {
            Table.Index.Predicate range = tryCreateRange(leftPred, rightPred, condition);
            if (range != null) {
                return range;
            }
            return new Table.Index.Predicate(
                leftPred.getFieldName(),
                leftPred.getCondition(),
                leftPred.getValue(),
                condition,
                rightPred
            );
        }
        return null;
    }

    private static Table.Index.Predicate parseBetween(String expr) {
        Pattern pattern = Pattern.compile(
            "^([\\w_]+)\\s+BETWEEN\\s+('[^']*'|\\d+)\\s+AND\\s+('[^']*'|\\d+)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(expr);
        if (matcher.matches()) {
            return new Table.Index.Predicate(
                matcher.group(1),
                Condition.BETWEEN,
                matcher.group(2).replaceAll("'", "") + " AND " + matcher.group(3).replaceAll("'", ""),
                null,
                null
            );
        }
        return null;
    }

    private static Table.Index.Predicate parseIn(String expr) {
        Pattern pattern = Pattern.compile(
            "^([\\w_]+)\\s+IN\\s*\\(\\s*([^)]+)\\s*\\)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(expr);
        if (matcher.matches()) {
            return new Table.Index.Predicate(
                matcher.group(1),
                Condition.IN,
                matcher.group(2).replaceAll("'", ""),
                null,
                null
            );
        }
        return null;
    }

    private static Table.Index.Predicate tryCreateRange(Table.Index.Predicate left, Table.Index.Predicate right, Condition op) {
        if (op != Condition.AND) {
            return null;
        }
        if (!left.getFieldName().equals(right.getFieldName())) {
            return null;
        }
        if ((left.getCondition() == Condition.GREATER || left.getCondition() == Condition.GREATER_OR_EQUAL) &&
            (right.getCondition() == Condition.LESS || right.getCondition() == Condition.LESS_OR_EQUAL)) {
            return createBetweenPredicate(left, right);
        } else if ((right.getCondition() == Condition.GREATER || right.getCondition() == Condition.GREATER_OR_EQUAL) &&
            (left.getCondition() == Condition.LESS || left.getCondition() == Condition.LESS_OR_EQUAL)) {
            return createBetweenPredicate(right, left);
        }
        return null;
    }

    private static Table.Index.Predicate createBetweenPredicate(Table.Index.Predicate lower, Table.Index.Predicate upper) {
        String lowerBound = lower.getCondition() == Condition.GREATER_OR_EQUAL ? ">= " : "> ";
        String upperBound = upper.getCondition() == Condition.LESS_OR_EQUAL ? "<= " : "< ";
        return new Table.Index.Predicate(
            lower.getFieldName(),
            Condition.BETWEEN,
            lowerBound + lower.getValue() + " AND " + upperBound + upper.getValue(),
            null,
            null
        );
    }

    private static Table.Index.Predicate parseSimple(String expr) {
        expr = expr.replaceAll("\\s+", " ").trim();
        Pattern typeCastPattern = Pattern.compile(
            "^([\\w_]+)\\s*(=|!=|>|<|>=|<=|LIKE|ILIKE)\\s*\\(([^)]+)\\)::[\\w]+\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher typeCastMatcher = typeCastPattern.matcher(expr);
        if (typeCastMatcher.matches()) {
            return new Table.Index.Predicate(
                typeCastMatcher.group(1),
                Condition.fromString(typeCastMatcher.group(2)),
                typeCastMatcher.group(3).replaceAll("'", ""),
                null,
                null
            );
        }
        Pattern boolPattern = Pattern.compile(
            "^([\\w_]+)\\s*(=|!=)\\s*(true|false)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher boolMatcher = boolPattern.matcher(expr);
        if (boolMatcher.matches()) {
            return new Table.Index.Predicate(
                boolMatcher.group(1),
                Condition.fromString(boolMatcher.group(2)),
                boolMatcher.group(3),
                null,
                null
            );
        }
        Pattern pattern = Pattern.compile(
            "^([\\w_]+)\\s*(=|!=|>|<|>=|<=|LIKE|ILIKE)\\s*('[^']*'|\\d+)\\s*$",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(expr);
        if (matcher.matches()) {
            return new Table.Index.Predicate(
                matcher.group(1),
                Condition.fromString(matcher.group(2)),
                matcher.group(3).replaceAll("'", ""),
                null,
                null
            );
        }
        return null;
    }
}