/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Defines an object that returns an ORDER BY column name.
 * @author erwinel
 */
@FunctionalInterface
public interface OrderBy extends Supplier<String> {
    
    /**
     * Determines if the column order is descending.
     * @return {@code true} if the order is descending; otherwise, {@code false} if it is ascending.
     */
    default boolean isDescending() { return false; }

    /**
     * Creates an ORDER BY object from a column name.
     * @param colName The name of the column to order by.
     * @param isDescending {@code true} if the order is descending; otherwise, {@code false} if it is ascending.
     * @return An ORDER BY object from a column name.
     */
    public static OrderBy of(String colName, boolean isDescending) {
        assert !Objects.requireNonNull(colName, "Column name cannot be null").trim().isEmpty() : "Column name cannot be empty";
        return new OrderBy() {
            @Override
            public boolean isDescending() { return isDescending; }

            @Override
            public String get() { return colName; }
        };
    }

    /**
     * Creates an ascending ORDER BY object
     * @param colName The name of the column to order by.
     * @return An ascending ORDER BY object
     */
    public static OrderBy of(String colName) {
        assert !Objects.requireNonNull(colName, "Column name cannot be null").trim().isEmpty() : "Column name cannot be empty";
        return () -> colName;
    }
    
    public static List<OrderBy> of(OrderBy ...items) {
        if (null == items || items.length == 0)
            return new ArrayList<>();
        return Arrays.asList(items);
    }
    /**
     * Builds an SQL ORDER BY clause from one or more {@link OrderBy} objects.
     * @param orderBy The {@link OrderBy} objects that define the ORDER BY clause.
     * @return The SQL ORDER BY clause from one or more {@link OrderBy} objects or an empty string if there is no ORDER BY clause.
     */
    public static String toSqlClause(Iterable<OrderBy> orderBy) {
        if (null == orderBy)
            return "";
        Iterator<OrderBy> it = orderBy.iterator();
        if (!it.hasNext())
            return "";
        OrderBy o = it.next();
        String colName = o.get();
        while (null == colName || colName.trim().isEmpty()) {
            if (!it.hasNext())
                return "";
            o = it.next();
            colName = o.get();
        }
        StringBuilder sb = new StringBuilder("ORDER BY `");
        sb.append(colName).append("`");
        if (o.isDescending())
            sb.append(" DESC");
        while (it.hasNext()) {
            o = it.next();
            colName = o.get();
            if (null != colName && !colName.isEmpty()) {
                sb.append(", `").append(colName).append("`");
                if (o.isDescending())
                    sb.append(" DESC");
            }
        }
        
        return sb.toString();
    }
    
    public static Iterable<OrderBy> getOrderByOrDefault(Iterable<OrderBy> orderBy, Supplier<Iterable<OrderBy>> ifEmpty) {
        if (OrderBy.toSqlClause(orderBy).isEmpty() && null != ifEmpty)
            return ifEmpty.get();
        return orderBy;
    }
    
    /**
     * Creates a {@link PreparedStatement} with parameters initialized.
     * @param connection The {@link Connection} for creating a new {@link PreparedStatement} object.
     * @param baseSQL The base SQL statement.
     * @param orderBy The objects that defined the ORDER BY clause to be appended to the {@code baseSQL} after the WHERE clause.
     * @return The {@link PreparedStatement} with parameters initialized.
     * @throws SQLException if not able to create or initialize the {@link PreparedStatement}.
     */
    public static PreparedStatement prepareStatement(Connection connection, String baseSQL, Iterable<OrderBy> orderBy) throws SQLException {
        assert (Objects.requireNonNull(baseSQL, "The base SQL statement cannot be null")).trim().isEmpty() : "The base SQL statement cannot be empty";
        StringBuilder sql = new StringBuilder(baseSQL);
        SqlConditional c;
        String s = OrderBy.toSqlClause(orderBy);
        if (s.length() > 0)
            sql.append(" ").append(s);
        return Objects.requireNonNull(connection, "Connection object cannot be null").prepareStatement(sql.toString());
    }
}
