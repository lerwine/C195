package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 */
@FunctionalInterface
public interface TriPredicate<T, U, S> {

    boolean test(T t, U u, S s);
}
