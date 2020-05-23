package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 * @param <R>
 */
@FunctionalInterface
public interface QuadFunction<T, U, S, V, R> {
    R apply(T t, U u, S s, V v);
}
