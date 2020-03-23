package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
@FunctionalInterface
public interface BiIntPredicate {
    boolean test(int t, int u);
}
