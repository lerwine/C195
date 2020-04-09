package scheduler.model;

import java.util.function.Supplier;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface SelectStatementColumn extends Supplier<String> {
    default String getAlias() { return get(); }
    public static boolean areEqual(SelectStatementColumn a, SelectStatementColumn b) {
        if (null == a)
            return null == b;
        return null != b && a.get().equals(b.get()) && a.getAlias().equals(b.getAlias());
    }
}
