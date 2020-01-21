/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.DataObject;
import util.ThrowableConsumer;
import view.ItemModel;
import view.address.CustomerAddress;
import view.city.AddressCity;
import view.country.CityCountry;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

/**
 * Defines an object that function as a {@link Predicate} for {@link ItemModel} objects and also can produce an SQL statement for use within
 * a WHERE clause to produce the same results from a database query.
 * @author erwinel
 * @param <M> The type of {@link ItemModel} that can be filtered.
 */
public interface ModelFilter<M extends ItemModel<?>> extends SqlConditional, Predicate<M>  {
    
    //<editor-fold defaultstate="collapsed" desc="Instance members">

    /**
     * Gets the column for the current conditional statement or an empty string if not applicable.
     * @return The column for the current conditional statement or an empty string if not applicable.
     */
    String getColName(); 
    
    /**
     * Gets the SQL operator string for the current conditional statement.
     * @return The SQL operator string for the current conditional statement.
     */
    String getOperator();

    /**
     * Allows the current filter to sequentially set the parameter values.
     * @param consumer The object that accepts parameter values.
     * @throws SQLException If unable to set one of the parameter values.
     */
    void setParameterValues(ParameterConsumer consumer) throws SQLException;
    
    @Override
    default String get() { return String.format("`%s`%s%%", getColName(), getOperator()); }
    
    /**
     *
     * @return
     */
    default SqlConditional toConditional() {
        if (isEmpty())
            return ModelFilter.empty();
        String s = get();
        return (null == s || s.trim().isEmpty()) ? ModelFilter.empty() : () -> s;
    }

    /**
     *
     * @param other
     * @return
     */
    default ModelFilter<M> and(ModelFilter<M> other) { return FilterAnd.combine(this, other); }

    /**
     *
     * @param other
     * @return
     */
    default ModelFilter<M> or(ModelFilter<M> other) { return FilterOr.combine(this, other); }

    /**
     *
     * @return
     */
    ModelFilter<M> makeClone();
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="buildStatement overloads">
    
    /**
     * Builds an SQL statement.
     * @param builder The abstract SQL statement builder.
     * @param baseSQL The base SQL statement.
     * @param filter The object that defines the WHERE clause and the associated parameters to be appended to the {@code baseSQL}.
     * @throws SQLException if not able to create or initialize the {@link PreparedStatement}.
     */
    public static void buildStatement(SqlStatementBuilder builder, String baseSQL, ModelFilter<?> filter) throws SQLException {
        buildStatement(builder, baseSQL, null, filter);
    }
    
    /**
     * Builds an SQL statement.
     * @param builder The abstract SQL statement builder.
     * @param baseSQL The base SQL statement.
     * @param filter The object that defines the WHERE clause and the associated parameters to be appended to the {@code baseSQL}.
     * @param orderBy The objects that defined the ORDER BY clause to be appended to the {@code baseSQL} after the WHERE clause.
     * @throws SQLException if not able to create or initialize the {@link PreparedStatement}.
     */
    public static void buildStatement(SqlStatementBuilder builder, String baseSQL, ModelFilter<?> filter, Iterable<OrderBy> orderBy) throws SQLException {
        buildStatement(builder, baseSQL, null, filter, orderBy);
    }
    
    /**
     * Builds an SQL statement.
     * @param builder The abstract SQL statement builder.
     * @param baseSQL The base SQL statement.
     * @param setParameters Sets any parameterized values defined in the {@code baseSQL}, returning the next sequential 1-based parameter index.
     * @param filter The object that defines the WHERE clause and the associated parameters to be appended to the {@code baseSQL}.
     * @throws SQLException if not able to create or initialize the {@link PreparedStatement}.
     */
    public static void buildStatement(SqlStatementBuilder builder, String baseSQL,
            ThrowableConsumer<ParameterConsumer, SQLException> setParameters,
            ModelFilter<?> filter) throws SQLException {
        buildStatement(builder, baseSQL, setParameters, filter, null);
    }
    
    /**
     * Builds an SQL statement.
     * @param builder The abstract SQL statement builder.
     * @param baseSQL The base SQL statement.
     * @param setParameters Sets any parameterized values defined in the {@code baseSQL}, returning the next sequential 1-based parameter index.
     * @param filter The object that defines the WHERE clause and the associated parameters to be appended to the {@code baseSQL}.
     * @param orderBy The objects that defined the ORDER BY clause to be appended to the {@code baseSQL} after the WHERE clause.
     * @throws SQLException if not able to build the SQL statement.
     */
    public static void buildStatement(SqlStatementBuilder builder, String baseSQL, ThrowableConsumer<ParameterConsumer, SQLException> setParameters,
            ModelFilter<?> filter, Iterable<OrderBy> orderBy) throws SQLException {
        assert (Objects.requireNonNull(baseSQL, "The base SQL statement cannot be null")).trim().isEmpty() : "The base SQL statement cannot be empty";
        builder.appendSql(baseSQL);
        SqlConditional c;
        String s;
        final ParameterConsumer consumer;
        if (null == filter || (c = (filter = filter.makeClone()).toConditional()).isEmpty()) {
            s = OrderBy.toSqlClause(orderBy);
            if (s.length() > 0)
                builder.appendSql(" ").appendSql(s);
            if (null != setParameters)
                setParameters.accept(builder.finalizeSql());
            else
                builder.finalizeSql();
        } else {
            builder.appendSql(" WHERE ").appendSql(c.get());
            s = OrderBy.toSqlClause(orderBy);
            if (s.length() > 0)
                builder.appendSql(" ").appendSql(s);
            consumer = builder.finalizeSql();
            if (null != setParameters)
                setParameters.accept(consumer);
            filter.setParameterValues(consumer);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static Comparator definitions">
    
    /**
     * A case-sensitive {@link Comparator} for string values.
     */
    public static final Comparator<String> COMPARATOR_STRING = (String o1, String o2) -> o1.compareTo(o2);
    
    /**
     * A {@link Comparator} for {@link LocalDateTime} values.
     */
    public static final Comparator<LocalDateTime> COMPARATOR_LOCALDATETIME = (LocalDateTime o1, LocalDateTime o2) -> o1.compareTo(o2);
    
    /**
     * A {@link Comparator} for {@link Integer} values.
     */
    public static final Comparator<Integer> COMPARATOR_INTEGER = (Integer o1, Integer o2) -> o1.compareTo(o2);
    
    /**
     * A {@link Comparator} for {@link Boolean} values.
     */
    public static final Comparator<Boolean> COMPARATOR_BOOLEAN = (Boolean o1, Boolean o2) -> o1.compareTo(o2);
    
    /**
     * A {@link Comparator} for {@link AppointmentCustomer} objects.
     */
    public static final Comparator<AppointmentCustomer<?>> COMPARATOR_CUSTOMER = (AppointmentCustomer<?> o1, AppointmentCustomer<?> o2) -> {
        if (o1 == null)
            return (o2 == null) ? 0 : 1;
        if (o2 == null)
            return -1;
        if (o1.getDataObject().getPrimaryKey() == o2.getDataObject().getPrimaryKey()) {
            if (o1.getDataObject().getRowState() == DataObject.ROWSTATE_NEW) {
                if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                    return -1;
            } else if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                return 1;
            return 0;
        }
        int result = o1.getName().compareTo(o2.getName());
        return (result == 0) ? o1.getDataObject().getPrimaryKey() - o2.getDataObject().getPrimaryKey() : result;
    };
    
    /**
     * A {@link Comparator} for {@link AppointmentUser} objects.
     */
    public static final Comparator<AppointmentUser<?>> COMPARATOR_USER = (AppointmentUser<?> o1, AppointmentUser<?> o2) -> {
        if (o1 == null)
            return (o2 == null) ? 0 : 1;
        if (o2 == null)
            return -1;
        if (o1.getDataObject().getPrimaryKey() == o2.getDataObject().getPrimaryKey()) {
            if (o1.getDataObject().getRowState() == DataObject.ROWSTATE_NEW) {
                if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                    return -1;
            } else if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                return 1;
            return 0;
        }
        int result = o1.getUserName().compareTo(o2.getUserName());
        return (result == 0) ? o1.getDataObject().getPrimaryKey() - o2.getDataObject().getPrimaryKey() : result;
    };
    
    /**
     * A {@link Comparator} for {@link CityCountry} objects.
     */
    public static final Comparator<CityCountry<?>> COMPARATOR_COUNTRY = (CityCountry<?> o1, CityCountry<?> o2) -> {
        if (o1 == null)
            return (o2 == null) ? 0 : 1;
        if (o2 == null)
            return -1;
        if (o1.getDataObject().getPrimaryKey() == o2.getDataObject().getPrimaryKey()) {
            if (o1.getDataObject().getRowState() == DataObject.ROWSTATE_NEW) {
                if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                    return -1;
            } else if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                return 1;
            return 0;
        }
        int result = o1.getName().compareTo(o2.getName());
        return (result == 0) ? o1.getDataObject().getPrimaryKey() - o2.getDataObject().getPrimaryKey() : result;
    };
    
    /**
     * A {@link Comparator} for {@link AddressCity} objects.
     */
    public static final Comparator<AddressCity<?>> COMPARATOR_CITY = (AddressCity<?> o1, AddressCity<?> o2) -> {
        if (o1 == null)
            return (o2 == null) ? 0 : 1;
        if (o2 == null)
            return -1;
        if (o1.getDataObject().getPrimaryKey() == o2.getDataObject().getPrimaryKey()) {
            if (o1.getDataObject().getRowState() == DataObject.ROWSTATE_NEW) {
                if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                    return -1;
            } else if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                return 1;
            return 0;
        }
        int result = o1.getName().compareTo(o2.getName());
        return (result == 0) ? COMPARATOR_COUNTRY.compare(o1.getCountry(), o2.getCountry()) : result;
    };
    
    /**
     * A {@link Comparator} for {@link CustomerAddress} objects.
     */
    public static final Comparator<CustomerAddress<?>> COMPARATOR_ADDRESS = (CustomerAddress<?> o1, CustomerAddress<?> o2) -> {
        if (o1 == null)
            return (o2 == null) ? 0 : 1;
        if (o2 == null)
            return -1;
        if (o1.getDataObject().getPrimaryKey() == o2.getDataObject().getPrimaryKey()) {
            if (o1.getDataObject().getRowState() == DataObject.ROWSTATE_NEW) {
                if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                    return -1;
            } else if (o2.getDataObject().getRowState() != DataObject.ROWSTATE_NEW)
                return 1;
            return 0;
        }
        int result = o1.getAddress1().compareTo(o2.getAddress1());
        if (result != 0 || (result = o1.getAddress2().compareTo(o2.getAddress2())) != 0)
            return result;
        
        AddressCity<?> a1 = o1.getCity();
        AddressCity<?> a2 = o2.getCity();
        if (a1 == null) {
            if (a2 != null)
                return 1;
        } else {
            if (a2 == null)
                return -1;
            if ((result = a1.getName().compareTo(a2.getName())) != 0)
                return result;
        }
        
        if (null != a1 && null != a2) {
            if ((result = o1.getPostalCode().compareTo(o2.getPostalCode())) != 0 ||
                    (result = COMPARATOR_COUNTRY.compare(a1.getCountry(), a2.getCountry())) != 0)
                return result;
        } else if ((result = o1.getPostalCode().compareTo(o2.getPostalCode())) != 0)
            return result;
        return o1.getDataObject().getPrimaryKey() - o2.getDataObject().getPrimaryKey();
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    /**
     * Creates a {@link ModelFilter} that matches where a column/property is equal to a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is equal to a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsEqualTo(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsEqualTo(accessor, comparator, value); }
            @Override
            public String getOperator() { return "="; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) == 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates a {@link ModelFilter} that matches where a column/property is not equal to a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is not equal to a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsNotEqualTo(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsEqualTo(accessor, comparator, value); }
            @Override
            public String getOperator() { return "<>"; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) != 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates a {@link ModelFilter} that matches where a column/property is greater than a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is greater than a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsGreaterThan(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsGreaterThan(accessor, comparator, value); }
            @Override
            public String getOperator() { return ">"; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) > 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates a {@link ModelFilter} that matches where a column/property is greater than or equal to a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is greater than or equal to a a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsGreaterThanOrEqualTo(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsGreaterThanOrEqualTo(accessor, comparator, value); }
            @Override
            public String getOperator() { return ">="; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) >= 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates a {@link ModelFilter} that matches where a column/property is less than a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is less than a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsLessThan(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsLessThan(accessor, comparator, value); }
            @Override
            public String getOperator() { return "<"; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) < 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates a {@link ModelFilter} that matches where a column/property is less than or equal to a specified value.
     * @param <V> The column value type.
     * @param <M> The {@link ItemModel} type.
     * @param accessor An object that gets and sets the values for the associated column/property.
     * @param comparator Compares the column/property values.
     * @param value The value to compare to.
     * @return A {@link ModelFilter} that matches where a column/property is less than or equal to a a specified value.
     */
    public static <V, M extends ItemModel<?>> ModelFilter<M> columnIsLessThanOrEqualTo(ValueAccessor<M, V> accessor, Comparator<V> comparator, V value) {
        return new ModelFilter<M>() {
            @Override
            public ModelFilter<M> makeClone() { return columnIsLessThanOrEqualTo(accessor, comparator, value); }
            @Override
            public String getOperator() { return "<="; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException {
                accessor.accept(value, consumer);
            }
            @Override
            public boolean test(M t) { return comparator.compare(accessor.apply(t), value) <= 0; }
            @Override
            public String getColName() { return accessor.get(); }
        };
    }
    
    /**
     * Creates an empty {@link ModelFilter} (matches anything).
     * @param <M> The {@link ItemModel} type.
     * @return An empty {@link ModelFilter} (matches anything).
     */
    public static <M extends ItemModel<?>> ModelFilter<M> empty() {
        return new ModelFilter<M>() {
            @Override
            public String getOperator() { return ""; }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException { }
            @Override
            public SqlConditional toConditional() { return this; }
            @Override
            public ModelFilter<M> and(ModelFilter<M> other) { return (null == other) ? this : other; }
            @Override
            public ModelFilter<M> or(ModelFilter<M> other) { return (null == other) ? this : other; }
            @Override
            public ModelFilter<M> makeClone() { return ModelFilter.empty(); }
            @Override
            public String get() { return ""; }
            @Override
            public boolean isEmpty() { return true; }
            @Override
            public boolean test(M t) { return true; }
            @Override
            public String getColName() { return ""; }
        };
    }
    
    //</editor-fold>
    
}
