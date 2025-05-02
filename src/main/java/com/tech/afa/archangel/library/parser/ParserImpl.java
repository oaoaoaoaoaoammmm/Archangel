package com.tech.afa.archangel.library.parser;

import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.model.enums.Condition;
import com.tech.afa.archangel.library.model.enums.SQLCommandType;
import com.tech.afa.archangel.library.model.enums.SQLJoinType;
import com.tech.afa.archangel.library.model.request.SQLCondition;
import com.tech.afa.archangel.library.model.request.SQLGroupBy;
import com.tech.afa.archangel.library.model.request.SQLJoin;
import com.tech.afa.archangel.library.model.request.SQLOrderBy;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.request.SQLValue;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ParserImpl implements Parser {

    @Override
    public SQLRequest parse(SQLRequestView sqlView) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sqlView.sql());
            Map<String, String> aliasToTableName = new HashMap<>();
            return createSqlRequest(sqlView, statement, aliasToTableName);
        } catch (Exception ex) {
            log.error("Failed to parse SQL: {}", sqlView.sql(), ex);
            return new SQLRequest(sqlView.id(), sqlView.sql(), SQLCommandType.UNKNOWN);
        }
    }

    private SQLRequest createSqlRequest(
        SQLRequestView sqlView,
        Statement statement,
        Map<String, String> aliasToTableName
    ) {
        if (statement instanceof Select select) {
            return parseSelectStatement(sqlView, select, aliasToTableName);
        } else if (statement instanceof Insert insert) {
            return parseInsertStatement(sqlView, insert, aliasToTableName);
        } else if (statement instanceof Update update) {
            return parseUpdateStatement(sqlView, update, aliasToTableName);
        } else if (statement instanceof Delete delete) {
            return parseDeleteStatement(sqlView, delete, aliasToTableName);
        }
        return new SQLRequest(sqlView.id(), sqlView.sql(), SQLCommandType.UNKNOWN);
    }

    private SQLRequest parseSelectStatement(
        SQLRequestView sqlView,
        Select select,
        Map<String, String> aliasToTableName
    ) {
        SQLRequest sqlRequest = createBaseRequest(sqlView, SQLCommandType.SELECT);
        SelectBody selectBody = select.getSelectBody();
        if (!(selectBody instanceof PlainSelect plainSelect)) {
            sqlRequest.setCommandType(SQLCommandType.UNKNOWN);
            return sqlRequest;
        }
        parseSelectComponents(plainSelect, sqlRequest, sqlView.id(), aliasToTableName);
        return sqlRequest;
    }

    private void parseSelectComponents(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        parseFromClause(plainSelect, sqlRequest, aliasToTableName);
        parseJoins(plainSelect, sqlRequest, requestId, aliasToTableName);
        parseSelectItems(plainSelect, sqlRequest, aliasToTableName);
        parseWhereClause(plainSelect, sqlRequest, requestId, aliasToTableName);
        parseOrderByClause(plainSelect, sqlRequest, aliasToTableName);
        parseGroupByClause(plainSelect, sqlRequest, aliasToTableName);
        parseHavingClause(plainSelect, sqlRequest, requestId, aliasToTableName);
        parseLimitClause(plainSelect, sqlRequest);
        sqlRequest.setAliasToExpression(aliasToTableName);
    }

    private SQLRequest parseInsertStatement(
        SQLRequestView sqlView,
        Insert insert,
        Map<String, String> aliasToTableName
    ) {
        SQLRequest sqlRequest = createBaseRequest(sqlView, SQLCommandType.INSERT);
        sqlRequest.setTables(List.of(insert.getTable().getName()));
        if (insert.getColumns() != null) {
            sqlRequest.setColumns(insert.getColumns().stream()
                .map(Column::getColumnName)
                .toList());
        }
        parseInsertValues(insert, sqlRequest, sqlView.id(), aliasToTableName);
        sqlRequest.setAliasToExpression(aliasToTableName);
        return sqlRequest;
    }

    private void parseInsertValues(
        Insert insert,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (insert.getItemsList() == null) {
            return;
        }
        List<List<SQLValue>> values = new ArrayList<>();
        if (insert.getItemsList() instanceof ExpressionList exprList) {
            values.add(parseExpressionList(exprList, requestId, aliasToTableName));
        } else if (insert.getItemsList() instanceof MultiExpressionList multiExprList) {
            multiExprList.getExpressionLists().forEach(exprList ->
                values.add(parseExpressionList(exprList, requestId, aliasToTableName)));
        }
        sqlRequest.setValues(values);
    }

    private List<SQLValue> parseExpressionList(
        ExpressionList exprList,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        return exprList.getExpressions().stream()
            .map(expr -> createSqlValue(expr, requestId, aliasToTableName))
            .collect(Collectors.toList());
    }

    private SQLValue createSqlValue(
        Expression expr,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        SQLValue sqlValue = new SQLValue();
        if (expr instanceof SubSelect subSelect) {
            sqlValue.setSelectRequest(parseSubSelect(subSelect, requestId, aliasToTableName));
        } else {
            sqlValue.setRawValue(expr.toString());
        }
        return sqlValue;
    }

    private SQLRequest parseUpdateStatement(
        SQLRequestView sqlView,
        Update update,
        Map<String, String> aliasToTableName) {
        SQLRequest sqlRequest = createBaseRequest(sqlView, SQLCommandType.UPDATE);
        sqlRequest.setTables(List.of(update.getTable().getName()));
        sqlRequest.setColumns(update.getColumns().stream()
            .map(Column::getColumnName)
            .toList());
        parseUpdateExpressions(update, sqlRequest, sqlView.id(), aliasToTableName);
        parseUpdateWhereClause(update, sqlRequest, sqlView.id(), aliasToTableName);
        sqlRequest.setAliasToExpression(aliasToTableName);
        return sqlRequest;
    }

    private void parseUpdateExpressions(
        Update update,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (update.getExpressions() != null && !update.getExpressions().isEmpty()) {
            List<SQLValue> valueRow = update.getExpressions().stream()
                .map(expr -> createSqlValue(expr, requestId, aliasToTableName))
                .toList();
            sqlRequest.setValues(List.of(valueRow));
        }
    }

    private void parseUpdateWhereClause(
        Update update,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (update.getWhere() != null) {
            sqlRequest.setWhereCondition(
                parseExpressionCondition(update.getWhere(), requestId, aliasToTableName));
        }
    }

    private SQLRequest parseDeleteStatement(
        SQLRequestView sqlView,
        Delete delete,
        Map<String, String> aliasToTableName
    ) {
        SQLRequest sqlRequest = createBaseRequest(sqlView, SQLCommandType.DELETE);
        sqlRequest.setTables(List.of(delete.getTable().getName()));
        if (delete.getWhere() != null) {
            sqlRequest.setWhereCondition(
                parseExpressionCondition(delete.getWhere(), sqlView.id(), aliasToTableName));
        }
        sqlRequest.setAliasToExpression(aliasToTableName);
        return sqlRequest;
    }

    private SQLRequest createBaseRequest(SQLRequestView sqlView, SQLCommandType commandType) {
        SQLRequest sqlRequest = new SQLRequest();
        sqlRequest.setId(sqlView.id());
        sqlRequest.setNativeSql(sqlView.sql());
        sqlRequest.setCommandType(commandType);
        return sqlRequest;
    }

    private void parseFromClause(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        Map<String, String> aliasToTableName
    ) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table table) {
            sqlRequest.setTables(List.of(table.getName()));
            registerTableAlias(table, aliasToTableName);
        } else if (fromItem instanceof SubSelect) {
            sqlRequest.setTables(List.of("SUBQUERY"));
        }
    }

    private void registerTableAlias(Table table, Map<String, String> aliasToTableName) {
        if (table.getAlias() != null) {
            aliasToTableName.put(table.getAlias().getName(), table.getName());
        }
    }

    private void parseSelectItems(PlainSelect plainSelect, SQLRequest sqlRequest,
                                  Map<String, String> aliasToTableName) {
        List<String> columns = plainSelect.getSelectItems().stream()
            .map(item -> processSelectItem(item, aliasToTableName))
            .toList();
        sqlRequest.setColumns(columns);
    }

    private String processSelectItem(SelectItem item, Map<String, String> aliasToTableName) {
        if (!(item instanceof SelectExpressionItem sei)) {
            return item.toString();
        }
        Expression expr = sei.getExpression();
        if (expr instanceof Function function) {
            return processFunctionExpression(function, aliasToTableName);
        } else if (expr instanceof Column column) {
            return processColumnExpression(column, aliasToTableName);
        } else if (expr instanceof AllColumns allColumns) {
            return processAllColumnsExpression(sei, allColumns, aliasToTableName);
        }
        return expr.toString();
    }

    private String processFunctionExpression(Function function, Map<String, String> aliasToTableName) {
        if (function.getParameters() == null) {
            return function.toString();
        }
        List<Expression> updatedParams = function.getParameters().getExpressions().stream()
            .map(param -> param instanceof Column column ?
                resolveColumnReference(column, aliasToTableName) : param)
            .collect(Collectors.toList());
        function.getParameters().setExpressions(updatedParams);
        return function.toString();
    }

    private Expression resolveColumnReference(Column column, Map<String, String> aliasToTableName) {
        String tableAlias = column.getTable() != null ? column.getTable().getName() : null;
        if (tableAlias != null && aliasToTableName.containsKey(tableAlias)) {
            return new Column(aliasToTableName.get(tableAlias) + "." + column.getColumnName());
        }
        return column;
    }

    private String processColumnExpression(Column column, Map<String, String> aliasToTableName) {
        String tableAlias = column.getTable() != null ? column.getTable().getName() : null;
        if (tableAlias != null && aliasToTableName.containsKey(tableAlias)) {
            return aliasToTableName.get(tableAlias) + "." + column.getColumnName();
        }
        return column.getColumnName();
    }

    private String processAllColumnsExpression(
        SelectExpressionItem sei,
        AllColumns allColumns,
        Map<String, String> aliasToTableName
    ) {
        String tableAlias = sei.getAlias() != null ? sei.getAlias().getName() : null;
        if (tableAlias != null && aliasToTableName.containsKey(tableAlias)) {
            return aliasToTableName.get(tableAlias) + ".*";
        }
        return "*";
    }

    private void parseJoins(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (plainSelect.getJoins() == null) {
            return;
        }
        List<SQLJoin> joins = plainSelect.getJoins().stream()
            .map(join -> createSqlJoin(join, requestId, aliasToTableName))
            .toList();
        sqlRequest.setJoins(joins);
    }

    private SQLJoin createSqlJoin(
        Join join,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        SQLJoin sqlJoin = new SQLJoin();
        FromItem rightItem = join.getRightItem();
        if (rightItem instanceof Table table) {
            sqlJoin.setJoinedTable(table.getName());
            registerTableAlias(table, aliasToTableName);
        } else if (rightItem instanceof SubSelect subSelect) {
            sqlJoin.setSubSelect(parseSubSelect(subSelect, requestId, new HashMap<>(aliasToTableName)));
        }
        sqlJoin.setType(determineJoinType(join));
        if (join.getOnExpression() != null) {
            sqlJoin.setCondition(parseExpressionCondition(join.getOnExpression(), requestId, aliasToTableName));
        }
        return sqlJoin;
    }

    private SQLJoinType determineJoinType(Join join) {
        if (join.isLeft()) {
            return SQLJoinType.LEFT;
        }
        if (join.isRight()) {
            return SQLJoinType.RIGHT;
        }
        if (join.isFull()) {
            return SQLJoinType.FULL;
        }
        if (join.isCross()) {
            return SQLJoinType.CROSS;
        }
        return SQLJoinType.INNER; // default
    }

    private void parseWhereClause(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (plainSelect.getWhere() != null) {
            sqlRequest.setWhereCondition(
                parseExpressionCondition(plainSelect.getWhere(), requestId, aliasToTableName));
        }
    }

    private void parseOrderByClause(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        Map<String, String> aliasToTableName
    ) {
        if (plainSelect.getOrderByElements() == null) {
            return;
        }
        List<SQLOrderBy> orderByList = plainSelect.getOrderByElements().stream()
            .map(obe -> createOrderByItem(obe, aliasToTableName))
            .toList();
        sqlRequest.setOrderBy(orderByList);
    }

    private SQLOrderBy createOrderByItem(OrderByElement obe, Map<String, String> aliasToTableName) {
        Expression expr = obe.getExpression();
        String columnName = expr instanceof Column column ?
            resolveColumnName(column, aliasToTableName) : expr.toString();
        return new SQLOrderBy(columnName, obe.isAsc());
    }

    private String resolveColumnName(Column column, Map<String, String> aliasToTableName) {
        String tableAlias = column.getTable() != null ? column.getTable().getName() : null;
        if (tableAlias != null && aliasToTableName.containsKey(tableAlias)) {
            return aliasToTableName.get(tableAlias) + "." + column.getColumnName();
        }
        return column.getColumnName();
    }

    private void parseGroupByClause(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        Map<String, String> aliasToTableName
    ) {
        if (plainSelect.getGroupBy() == null) {
            return;
        }
        List<SQLGroupBy> groupColumns = plainSelect.getGroupBy().getGroupByExpressions().stream()
            .map(expr -> new SQLGroupBy(resolveExpressionName(expr, aliasToTableName)))
            .toList();
        sqlRequest.setGroupBy(groupColumns);
    }

    private String resolveExpressionName(Expression expr, Map<String, String> aliasToTableName) {
        return expr instanceof Column column ?
            resolveColumnName(column, aliasToTableName) : expr.toString();
    }

    private void parseHavingClause(
        PlainSelect plainSelect,
        SQLRequest sqlRequest,
        String requestId,
        Map<String, String> aliasToTableName
    ) {
        if (plainSelect.getHaving() != null) {
            sqlRequest.setHavingCondition(
                parseExpressionCondition(plainSelect.getHaving(), requestId, aliasToTableName));
        }
    }

    private void parseLimitClause(PlainSelect plainSelect, SQLRequest sqlRequest) {
        if (plainSelect.getLimit() != null && plainSelect.getLimit().getRowCount() instanceof LongValue longValue) {
            sqlRequest.setLimit((int) longValue.getValue());
        }
    }

    private SQLRequest parseSubSelect(
        SubSelect subSelect,
        String parentId,
        Map<String, String> aliasToTableName
    ) {
        SelectBody selectBody = subSelect.getSelectBody();
        if (!(selectBody instanceof PlainSelect)) {
            return null;
        }
        Select subRoot = new Select();
        subRoot.setSelectBody(selectBody);
        return parseSelectStatement(
            new SQLRequestView(parentId + "_sub", subSelect.toString()),
            subRoot,
            aliasToTableName
        );
    }

    private void parseBinaryCondition(
        BinaryExpression expression,
        SQLCondition condition,
        Map<String, String> aliasToTableName
    ) {
        if (expression.getLeftExpression() != null) {
            if (expression.getLeftExpression() instanceof BinaryExpression) {
                return;
            }
            if (expression.getLeftExpression() instanceof Column column) {
                condition.setFieldName(resolveColumnName(column, aliasToTableName));
            } else {
                condition.setFieldName(expression.getLeftExpression().toString());
            }
        }
        switch (expression) {
            case EqualsTo ignore -> condition.setCondition(Condition.EQUALS);
            case NotEqualsTo ignore -> condition.setCondition(Condition.NOT_EQUALS);
            case GreaterThan ignore -> condition.setCondition(Condition.GREATER);
            case GreaterThanEquals ignore -> condition.setCondition(Condition.GREATER_OR_EQUAL);
            case MinorThan ignore -> condition.setCondition(Condition.LESS);
            case MinorThanEquals ignore -> condition.setCondition(Condition.LESS_OR_EQUAL);
            case LikeExpression ignore -> condition.setCondition(Condition.LIKE);
            case AndExpression ignore -> condition.setCondition(Condition.AND);
            case OrExpression ignore -> condition.setCondition(Condition.OR);
            default -> condition.setCondition(Condition.UNKNOWN);
        }
        if (expression.getRightExpression() != null) {
            if (expression.getRightExpression() instanceof Column column) {
                condition.setValue(resolveColumnName(column, aliasToTableName));
            } else {
                condition.setValue(expression.getRightExpression().toString());
            }
        }
    }

    private SQLCondition parseExpressionCondition(
        Expression expression,
        String id,
        Map<String, String> aliasToTableName
    ) {
        SQLCondition condition = new SQLCondition();
        if (expression instanceof BinaryExpression binaryExpression) {
            parseBinaryCondition(binaryExpression, condition, aliasToTableName);
        }
        condition.setExpression(resolveAliasesInExpression(expression.toString(), aliasToTableName));
        switch (expression) {
            case AndExpression andExpr ->
                condition.setAndConditions(parseAndOrConditions(andExpr, id, aliasToTableName));
            case OrExpression orExpr -> condition.setOrConditions(parseAndOrConditions(orExpr, id, aliasToTableName));
            case ExistsExpression existsExpr -> {

                parseExistsCondition(existsExpr, condition, id, aliasToTableName);
            }
            case InExpression inExpr -> parseInCondition(inExpr, condition, id, aliasToTableName);
            default -> expression.accept(new SubSelectVisitor(condition, id, aliasToTableName));
        }
        return condition;
    }

    private String resolveAliasesInExpression(String expression, Map<String, String> aliasToTableName) {
        String result = expression;
        for (Map.Entry<String, String> entry : aliasToTableName.entrySet()) {
            result = result.replace(entry.getKey() + ".", entry.getValue() + ".");
        }
        return result;
    }

    private List<SQLCondition> parseAndOrConditions(
        BinaryExpression expr,
        String id,
        Map<String, String> aliasToTableName
    ) {
        return List.of(
            parseExpressionCondition(expr.getLeftExpression(), id, aliasToTableName),
            parseExpressionCondition(expr.getRightExpression(), id, aliasToTableName)
        );
    }

    private void parseExistsCondition(
        ExistsExpression existsExpr,
        SQLCondition condition,
        String id,
        Map<String, String> aliasToTableName
    ) {
        if (existsExpr.getRightExpression() instanceof SubSelect subSelect) {
            condition.setSubSelect(parseSubSelect(subSelect, id + "_exists", new HashMap<>(aliasToTableName)));
        }
    }

    private void parseInCondition(
        InExpression inExpr,
        SQLCondition condition,
        String id,
        Map<String, String> aliasToTableName
    ) {
        if (inExpr.getRightItemsList() instanceof SubSelect subSelect) {
            condition.setSubSelect(parseSubSelect(subSelect, id + "_in", new HashMap<>(aliasToTableName)));
        }
    }

    @AllArgsConstructor
    private static class SubSelectVisitor extends ExpressionVisitorAdapter {
        private final SQLCondition condition;
        private final String id;
        private final Map<String, String> aliasToTableName;

        @Override
        public void visit(SubSelect subSelect) {
            condition.setSubSelect(new ParserImpl().parseSubSelect(subSelect, id + "_sub", aliasToTableName));
        }
    }
}