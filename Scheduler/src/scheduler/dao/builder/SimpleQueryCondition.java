/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.builder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 *
 * @author erwinel
 * @param <T>
 */
public interface SimpleQueryCondition<T> extends QueryCondition<SimpleOperator>, SelectColumn {
    T getValue();
    SupportedDbType getType();
    boolean isNull();
    static <T> SimpleQueryCondition<T> create(String tableName, String colName, SimpleOperator op, SupportedDbType type, T value) {
        Objects.requireNonNull(colName, "Column name cannot be null");
        assert !colName.trim().isEmpty() : "Column name cannot be empty";
        return new SimpleQueryCondition<T>() {
            private final String t = (tableName == null || tableName.trim().isEmpty()) ? "" : tableName;
            @Override
            public T getValue() { return value; }
            @Override
            public SupportedDbType getType() { return type; }
            @Override
            public boolean isNull() { return true; }
            @Override
            public SimpleOperator getOperator() { return op; }
            @Override
            public String getColName() { return colName; }
            @Override
            public String getTableName() { return tableName; }
        };
    }
    public static SimpleQueryCondition<String> stringIsNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.STRING, "");
    }
    public static SimpleQueryCondition<String> stringIsNull(String colName) { return stringIsNull(null, colName); }
    public static SimpleQueryCondition<String> stringNotNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.STRING, "");
    }
    public static SimpleQueryCondition<String> stringNotNull(String colName) { return stringNotNull(null, colName); }
    public static SimpleQueryCondition<String> stringEqualTo(String tableName, String colName, String value) {
        if (value == null)
            return stringIsNull(tableName, colName);
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringEqualTo(String colName, String value) { return stringEqualTo(null, colName, value); }
    public static SimpleQueryCondition<String> stringNotEqualTo(String tableName, String colName, String value) {
        if (value == null)
            return stringNotNull(tableName, colName);
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringNotEqualTo(String colName, String value) { return stringNotEqualTo(null, colName, value); }
    public static SimpleQueryCondition<String> stringGreaterThan(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.GREATER_THAN, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringGreaterThan(String colName, String value) { return stringGreaterThan(null, colName, value); }
    public static SimpleQueryCondition<String> stringGreaterThanOrEqualTo(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_LESS_THAN, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringGreaterThanOrEqualTo(String colName, String value) { return stringGreaterThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<String> stringLessThan(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.LESS_THAN, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringLessThan(String colName, String value) { return stringLessThan(null, colName, value); }
    public static SimpleQueryCondition<String> stringLessThanOrEqualTo(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_GREATER_THAN, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringLessThanOrEqualTo(String colName, String value) { return stringLessThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<String> stringLike(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.LIKE, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringLike(String colName, String value) { return stringLike(null, colName, value); }
    public static SimpleQueryCondition<String> stringNotLike(String tableName, String colName, String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_LIKE, SupportedDbType.STRING, value);
    }
    public static SimpleQueryCondition<String> stringNotLike(String colName, String value) { return stringNotLike(null, colName, value); }
    public static SimpleQueryCondition<Integer> intIsNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.INTEGER, 0);
    }
    public static SimpleQueryCondition<Integer> intIsNull(String colName) { return intIsNull(null, colName); }
    public static SimpleQueryCondition<Integer> intNotNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.INTEGER, 0);
    }
    public static SimpleQueryCondition<Integer> intNotNull(String colName) { return intNotNull(null, colName); }
    public static SimpleQueryCondition<Integer> intEqualTo(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intEqualTo(String colName, int value) { return intEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Integer> intNotEqualTo(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intNotEqualTo(String colName, int value) { return intNotEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Integer> intGreaterThan(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.GREATER_THAN, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intGreaterThan(String colName, int value) { return intGreaterThan(null, colName, value); }
    public static SimpleQueryCondition<Integer> intGreaterThanOrEqualTo(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.NOT_LESS_THAN, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intGreaterThanOrEqualTo(String colName, int value) { return intGreaterThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Integer> intLessThan(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.LESS_THAN, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intLessThan(String colName, int value) { return intLessThan(null, colName, value); }
    public static SimpleQueryCondition<Integer> intLessThanOrEqualTo(String tableName, String colName, int value) {
        return create(tableName, colName, SimpleOperator.NOT_GREATER_THAN, SupportedDbType.INTEGER, value);
    }
    public static SimpleQueryCondition<Integer> intLessThanOrEqualTo(String colName, int value) { return intLessThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Boolean> booleanIsNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.BOOLEAN, false);
    }
    public static SimpleQueryCondition<Boolean> booleanIsNull(String colName) { return booleanIsNull(null, colName); }
    public static SimpleQueryCondition<Boolean> booleanNotNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.BOOLEAN, false);
    }
    public static SimpleQueryCondition<Boolean> booleanNotNull(String colName) { return booleanNotNull(null, colName); }
    public static SimpleQueryCondition<Boolean> booleanEqualTo(String tableName, String colName, boolean value) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.BOOLEAN, false);
    }
    public static SimpleQueryCondition<Boolean> booleanEqualTo(String colName, boolean value) { return booleanEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Boolean> booleanNotEqualTo(String tableName, String colName, boolean value) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.BOOLEAN, false);
    }
    public static SimpleQueryCondition<Boolean> booleanNotEqualTo(String colName, boolean value) { return booleanNotEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampIsNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.TIMESTAMP, Timestamp.valueOf(LocalDateTime.MIN));
    }
    public static SimpleQueryCondition<Timestamp> timestampIsNull(String colName) { return timestampIsNull(null, colName); }
    public static SimpleQueryCondition<Timestamp> timestampNotNull(String tableName, String colName) {
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.TIMESTAMP, Timestamp.valueOf(LocalDateTime.MIN));
    }
    public static SimpleQueryCondition<Timestamp> timestampNotNull(String colName) { return timestampNotNull(null, colName); }
    public static SimpleQueryCondition<Timestamp> timestampEqualTo(String tableName, String colName, Timestamp value) {
        if (value == null)
            return timestampIsNull(tableName, colName);
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.TIMESTAMP, Timestamp.valueOf(LocalDateTime.MIN));
    }
    public static SimpleQueryCondition<Timestamp> timestampEqualTo(String colName, Timestamp value) { return timestampEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampNotEqualTo(String tableName, String colName, Timestamp value) {
        if (value == null)
            return timestampNotNull(tableName, colName);
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.TIMESTAMP, value);
    }
    public static SimpleQueryCondition<Timestamp> timestampNotEqualTo(String colName, Timestamp value) { return timestampNotEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampGreaterThan(String tableName, String colName, Timestamp value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.GREATER_THAN, SupportedDbType.TIMESTAMP, value);
    }
    public static SimpleQueryCondition<Timestamp> timestampGreaterThan(String colName, Timestamp value) { return timestampGreaterThan(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampGreaterThanOrEqualTo(String tableName, String colName, Timestamp value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_LESS_THAN, SupportedDbType.TIMESTAMP, value);
    }
    public static SimpleQueryCondition<Timestamp> timestampGreaterThanOrEqualTo(String colName, Timestamp value) { return timestampGreaterThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampLessThan(String tableName, String colName, Timestamp value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.LESS_THAN, SupportedDbType.TIMESTAMP, value);
    }
    public static SimpleQueryCondition<Timestamp> timestampLessThan(String colName, Timestamp value) { return timestampLessThan(null, colName, value); }
    public static SimpleQueryCondition<Timestamp> timestampLessThanOrEqualTo(String tableName, String colName, Timestamp value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_GREATER_THAN, SupportedDbType.TIMESTAMP, value);
    }
    public static SimpleQueryCondition<Timestamp> timestampLessThanOrEqualTo(String colName, Timestamp value) { return timestampLessThanOrEqualTo(null, colName, value); }
    public static SimpleQueryCondition<String> expressionEqualTo(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.EQUAL_TO, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionEqualTo(String colName, String sqlExpression) { return expressionEqualTo(null, colName, sqlExpression); }
    public static SimpleQueryCondition<String> expressionNotEqualTo(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_EQUAL_TO, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionNotEqualTo(String colName, String sqlExpression) { return expressionNotEqualTo(null, colName, sqlExpression); }
    public static SimpleQueryCondition<String> expressionGreaterThan(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.GREATER_THAN, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionGreaterThan(String colName, String sqlExpression) { return expressionGreaterThan(null, colName, sqlExpression); }
    public static SimpleQueryCondition<String> expressionGreaterThanOrEqualTo(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_LESS_THAN, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionGreaterThanOrEqualTo(String colName, String sqlExpression) { return expressionGreaterThanOrEqualTo(null, colName, sqlExpression); }
    public static SimpleQueryCondition<String> expressionLessThan(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.LESS_THAN, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionLessThan(String colName, String sqlExpression) { return expressionLessThan(null, colName, sqlExpression); }
    public static SimpleQueryCondition<String> expressionLessThanOrEqualTo(String tableName, String colName, String sqlExpression) {
        Objects.requireNonNull(sqlExpression, "SQL expression cannot be null");
        return create(tableName, colName, SimpleOperator.NOT_GREATER_THAN, SupportedDbType.EXPRESSION, sqlExpression);
    }
    public static SimpleQueryCondition<String> expressionLessThanOrEqualTo(String colName, String sqlExpression) { return expressionLessThanOrEqualTo(null, colName, sqlExpression); }
}
