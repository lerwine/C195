package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 */
@FunctionalInterface
public interface QuadPredicate<T, U, S, V> {

    boolean test(T t, U u, S s, V v);
}
