package scheduler.dao.dml;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.ValueType;
import scheduler.util.ReadOnlyList;

/**
 * A {@link TableReference} that includes references to {@link DbColumn}s.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <E> The type of {@link ColumnReference} contained in this list.
 */
public interface TableColumnList<E extends ColumnReference> extends TableReference, ReadOnlyList<E> {
    // TODO: Check all implementations. This should be a list that represents all columns from all joined tables.
    
    /**
     * Attempts to read a non-null string value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default Optional<String> tryGetString(ResultSet resultSet, E column) throws SQLException {
        assert column.getColumn().getType().getValueType() == ValueType.STRING : "Column type mismatch";
        String result = resultSet.getString(column.getName());
        return (resultSet.wasNull() || null == result) ? Optional.empty() : Optional.of(result);
    }
    
    /**
     * Attempts to read a non-null string value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default Optional<String> tryGetString(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column, includeRelated);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetString(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null string value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default Optional<String> tryGetString(ResultSet resultSet, DbColumn column) throws SQLException {
        return tryGetString(resultSet, column, false);
    }
    
    /**
     * Attempts to read a non-null string value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param name The column {@link DbName} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default Optional<String> tryGetString(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetString(resultSet, columnReference);
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default String getString(ResultSet resultSet, E column, String defaultValue) throws SQLException {
        Optional<String> result = tryGetString(resultSet, column);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING} or the result value was null.
     */
    default String getString(ResultSet resultSet, E column) throws SQLException {
        Optional<String> result = tryGetString(resultSet, column);
        assert result.isPresent() : String.format("%s was null", column.getName());
        return result.get();
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default String getString(ResultSet resultSet, DbColumn column, String defaultValue, boolean includeRelated) throws SQLException {
        Optional<String> result = tryGetString(resultSet, column, includeRelated);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING} or the result value was null.
     */
    default String getString(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column, includeRelated);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getString(resultSet, columnReference);
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default String getString(ResultSet resultSet, DbColumn column, String defaultValue) throws SQLException {
        return getString(resultSet, column, defaultValue, false);
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING} or the result value was null.
     */
    default String getString(ResultSet resultSet, DbColumn column) throws SQLException {
        return getString(resultSet, column, false);
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING}.
     */
    default String getString(ResultSet resultSet, DbName name, String defaultValue) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getString(resultSet, columnReference, defaultValue);
    }
    
    /**
     * Gets a string value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#STRING} or the result value was null.
     */
    default String getString(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getString(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null integer value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default Optional<Integer> tryGetInt(ResultSet resultSet, E column) throws SQLException {
        assert column.getColumn().getType().getValueType() == ValueType.INT : "Column type mismatch";
        int result = resultSet.getInt(column.getName());
        return (resultSet.wasNull()) ? Optional.empty() : Optional.of(result);
    }
    
    /**
     * Attempts to read a non-null integer value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default Optional<Integer> tryGetInt(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column, includeRelated);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetInt(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null integer value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default Optional<Integer> tryGetInt(ResultSet resultSet, DbColumn column) throws SQLException {
        return tryGetInt(resultSet, column, false);
    }
    
    /**
     * Attempts to read a non-null integer value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default Optional<Integer> tryGetInt(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetInt(resultSet, columnReference);
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default int getInt(ResultSet resultSet, E column, int defaultValue) throws SQLException {
        Optional<Integer> result = tryGetInt(resultSet, column);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT} or the result value was null.
     */
    default int getInt(ResultSet resultSet, E column) throws SQLException {
        Optional<Integer> result = tryGetInt(resultSet, column);
        assert result.isPresent() : String.format("%s was null", column.getName());
        return result.get();
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default int getInt(ResultSet resultSet, DbColumn column, int defaultValue, boolean includeRelated) throws SQLException {
        Optional<Integer> result = tryGetInt(resultSet, column, includeRelated);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT} or the result value was null.
     */
    default int getInt(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getInt(resultSet, columnReference);
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default int getInt(ResultSet resultSet, DbColumn column, int defaultValue) throws SQLException {
        return getInt(resultSet, column, defaultValue, false);
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT} or the result value was null.
     */
    default int getInt(ResultSet resultSet, DbColumn column) throws SQLException {
        return getInt(resultSet, column, false);
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT}.
     */
    default int getInt(ResultSet resultSet, DbName name, int defaultValue) throws SQLException {
        Optional<Integer> result = tryGetInt(resultSet, name);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets an integer value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#INT} or the result value was null.
     */
    default int getInt(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getInt(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null boolean value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default Optional<Boolean> tryGetBoolean(ResultSet resultSet, E column) throws SQLException {
        assert column.getColumn().getType().getValueType() == ValueType.BOOLEAN : "Column type mismatch";
        boolean result = resultSet.getBoolean(column.getName());
        return (resultSet.wasNull()) ? Optional.empty() : Optional.of(result);
    }
    
    /**
     * Attempts to read a non-null boolean value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default Optional<Boolean> tryGetBoolean(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column, includeRelated);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetBoolean(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null boolean value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default Optional<Boolean> tryGetBoolean(ResultSet resultSet, DbColumn column) throws SQLException {
        return tryGetBoolean(resultSet, column, false);
    }
    
    /**
     * Attempts to read a non-null boolean value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a integer value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default Optional<Boolean> tryGetBoolean(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetBoolean(resultSet, columnReference);
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default boolean getBoolean(ResultSet resultSet, E column, boolean defaultValue) throws SQLException {
        Optional<Boolean> result = tryGetBoolean(resultSet, column);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN} or the result value was null.
     */
    default boolean getBoolean(ResultSet resultSet, E column) throws SQLException {
        Optional<Boolean> result = tryGetBoolean(resultSet, column);
        assert result.isPresent() : String.format("%s was null", column.getName());
        return result.get();
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default boolean getBoolean(ResultSet resultSet, DbColumn column, boolean defaultValue, boolean includeRelated) throws SQLException {
        Optional<Boolean> result = tryGetBoolean(resultSet, column, includeRelated);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default boolean getBoolean(ResultSet resultSet, DbColumn column, boolean defaultValue) throws SQLException {
        return getBoolean(resultSet, column, defaultValue, false);
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN} or the result value was null.
     */
    default boolean getBoolean(ResultSet resultSet, DbColumn column) throws SQLException {
        return getBoolean(resultSet, column, false);
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN}.
     */
    default boolean getBoolean(ResultSet resultSet, DbName name, boolean defaultValue) throws SQLException {
        Optional<Boolean> result = tryGetBoolean(resultSet, name);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a boolean value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#BOOLEAN or the result value was null}.
     */
    default boolean getBoolean(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getBoolean(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null {@link Timestamp} value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Optional<Timestamp> tryGetTimestamp(ResultSet resultSet, E column) throws SQLException {
        assert column.getColumn().getType().getValueType() == ValueType.TIMESTAMP : "Column type mismatch";
        Timestamp result = resultSet.getTimestamp(column.getName());
        return (resultSet.wasNull() || null == result) ? Optional.empty() : Optional.of(result);
    }
    
    /**
     * Attempts to read a non-null {@link Timestamp} value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Optional<Timestamp> tryGetTimestamp(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column, includeRelated);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetTimestamp(resultSet, columnReference);
    }
    
    /**
     * Attempts to read a non-null {@link Timestamp} value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Optional<Timestamp> tryGetTimestamp(ResultSet resultSet, DbColumn column) throws SQLException {
        return tryGetTimestamp(resultSet, column, false);
    }
    
    /**
     * Attempts to read a non-null {@link Timestamp} value from a {@link ResultSet}.
     * 
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return An {@link Optional} value containing a string value if the value from the was not null; {@link Optional#EMPTY} if it was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Optional<Timestamp> tryGetTimestamp(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return tryGetTimestamp(resultSet, columnReference);
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Timestamp getTimestamp(ResultSet resultSet, E column, Timestamp defaultValue) throws SQLException {
        Optional<Timestamp> result = tryGetTimestamp(resultSet, column);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link ColumnReference} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP} or the result value was null.
     */
    default Timestamp getTimestamp(ResultSet resultSet, E column) throws SQLException {
        Optional<Timestamp> result = tryGetTimestamp(resultSet, column);
        assert result.isPresent() : String.format("%s was null", column.getName());
        return result.get();
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbColumn column, Timestamp defaultValue, boolean includeRelated) throws SQLException {
        Optional<Timestamp> result = tryGetTimestamp(resultSet, column, includeRelated);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param includeRelated {@code true} to get a value from related column as well;
     * otherwise {@code false} to only get data strictly from that column.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP} or the result value was null.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbColumn column, boolean includeRelated) throws SQLException {
        E columnReference = findFirst(column);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getTimestamp(resultSet, columnReference);
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbColumn column, Timestamp defaultValue) throws SQLException {
        return getTimestamp(resultSet, column, defaultValue, false);
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param column The {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP} or the result value was null.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbColumn column) throws SQLException {
        return getTimestamp(resultSet, column, false);
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @param defaultValue The default value to return if the value retrieved from the {@link ResultSet} was null.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP}.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbName name, Timestamp defaultValue) throws SQLException {
        Optional<Timestamp> result = tryGetTimestamp(resultSet, name);
        return (result.isPresent()) ? result.get() : defaultValue;
    }
    
    /**
     * Gets a {@link Timestamp} value from a {@link ResultSet}.
     * @param resultSet The source {@link ResultSet}.
     * @param name The {@link DbName} of the {@link DbColumn} to retrieve a value for.
     * @return The value from the {@link ResultSet} or {@code ifNull} if the retrieved value was null.
     * @throws SQLException if unable to read data from the {@link ResultSet}.
     * @throws AssertionError if the {@link scheduler.dao.schema.ColumnType#valueType} associated with the {@code column} {@link DbColumn#type}
     * is not {@link ValueType#TIMESTAMP} or the result value was null.
     */
    default Timestamp getTimestamp(ResultSet resultSet, DbName name) throws SQLException {
        E columnReference = findFirstColumn(name);
        if (null == columnReference)
            throw new NoSuchElementException("Column reference not found");
        return getTimestamp(resultSet, columnReference);
    }
    
    /**
     * Gets the first {@link ColumnReference} that refers to the specified {@link DbColumn}.
     * 
     * @param column The {@link DbColumn} to look for.
     * @param includeRelated
     * @return The first {@link ColumnReference} that refers to the specified {@link DbColumn} or {@code null} if no match was found.
     */
    default E findFirst(DbColumn column, boolean includeRelated) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (e.getColumn() == column)
                return e;
        }
        if (includeRelated) {
            it = iterator();
            while (it.hasNext()) {
                E e = it.next();
                if (SchemaHelper.areColumnsRelated(e.getColumn(), column))
                    return e;
            }
        }
        return null;
    }
    
    default E findFirst(DbColumn column) {
        return findFirst(column, false);
    }
    
    /**
     * Gets the first {@link ColumnReference} of a column that uses to the specified {@link DbName}.
     * 
     * @param name The {@link DbName} of the {@link DbColumn} to look for.
     * @return The first {@link ColumnReference} of a column that uses to the specified {@link DbName} or {@code null} if no match was found.
     */
    default E findFirstColumn(DbName name) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (e.getColumn().getDbName() == name)
                return e;
        }
        return null;
    }
    
    /**
     * Gets all {@link ColumnReference}s that refer to the specified {@link DbColumn}.
     * 
     * @param column The {@link DbColumn} to look for.
     * @return All {@link ColumnReference}s that refer to the specified {@link DbColumn}.
     */
    default Stream<E> findAll(DbColumn column) {
        if (null != column) {
            return stream().filter((t) -> t.getColumn() == column);
        }
        return Stream.empty();
    }
    
    /**
     * Gets all {@link ColumnReference}s for columns that use the specified {@link DbName}.
     * 
     * @param name The {@link DbName} to look for.
     * @return All {@link ColumnReference}s for columns that use the specified {@link DbName}.
     */
    default Stream<E> findAllColumns(DbName name) {
        if (null != name) {
            return stream().filter((t) -> t.getColumn().getDbName() == name);
        }
        return Stream.empty();
    }
    
    /**
     * Checks whether a column reference name is being used for any columns of the current list or in any child joins (if applicable).
     * 
     * @param name The column reference name to search for.
     * @return {@code true} if the column reference {@code name} is being used for any columns of the current list or in any child joins;
     * otherwise, {@code false}.
     */
    default boolean isColumnRefNameUsed(String name) {
        return null != name && !name.trim().isEmpty() && stream().anyMatch((t) -> t.getName().equalsIgnoreCase(name));
    }
    
    /**
     * Gets the {@link ColumnReference} that is referenced by a specified name within the current list or in any joined tables (if applicable).
     * 
     * @param name The column reference name to search for.
     * @return The {@link ColumnReference} that is referenced by the specified {@code name} or {@code null} if no match was found within the
     * current list or in any joined tables.
     */
    default E get(String name) {
        // TODO: Check all implementations since the usage has changed.
        if (null != name && !name.trim().isEmpty()) {
            Optional<E> result = stream().filter((t) -> t.getName().equalsIgnoreCase(name)).findFirst();
            if (result.isPresent())
                return result.get();
        }
        return null;
    }
    
}
