package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import scheduler.dao.DataAccessObject;

/**
 * Base interface for DAO filter expressions.
 * This is used to generate DML conditional statements and as a filtering predicate for {@link DataAccessObject}s.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The type of {@link DataAccessObject}.
 */
public interface DaoFilterExpression<T extends DataAccessObject> extends Predicate<T> {

    /**
     * Append the conditional statement for a DML query that contains only one table.
     * 
     * @param sb The {@link StringBuffer} to append to.
     */
    void appendSimpleDmlConditional(StringBuffer sb);

    /**
     * Append the conditional statement for a DML query that contains one or more joined tables.
     * 
     * @param sb The {@link StringBuffer} to append to.
     */
    void appendJoinedDmlConditional(StringBuffer sb);

    int applyWhereParameters(PreparedStatement ps, int index) throws SQLException;

    default boolean isEmpty() {
        return false;
    }

    public static <T extends DataAccessObject, U extends DaoFilterExpression<T>> U empty() {
        @SuppressWarnings("unchecked")
        U expr = (U) (new DaoFilterExpression<T>() {
            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(T t) {
                return true;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof DaoFilterExpression && ((DaoFilterExpression<? extends DataAccessObject>) obj).isEmpty();
            }
        });
        return expr;
    }
}
